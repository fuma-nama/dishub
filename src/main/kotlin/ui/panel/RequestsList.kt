package ui.panel

import bjda.plugins.ui.hook.ButtonClick.Companion.onClick
import bjda.ui.component.Content
import bjda.ui.component.Text
import bjda.ui.component.TextType
import bjda.ui.component.action.Button
import bjda.ui.component.row.Row
import bjda.ui.core.FComponent.Companion.component
import bjda.ui.core.IProps
import bjda.ui.core.rangeTo
import bjda.utils.convert
import bjda.utils.embed
import bjda.utils.field
import kotlinx.coroutines.launch
import models.tables.records.RequestInfoRecord
import models.tables.records.RequestRecord
import net.dv8tion.jda.api.entities.User
import net.dv8tion.jda.api.interactions.callbacks.IMessageEditCallback
import org.jooq.Record2
import org.jooq.Result
import utils.EventCoroutine
import utils.UIStore
import utils.getJumpUrl
import utils.queueAsync
import variables.RequestState

typealias Requests = Result<Record2<RequestRecord, RequestInfoRecord>>

class RequestsListProps : IProps() {
    lateinit var requests: Requests
    lateinit var next: suspend (offset: Int) -> Requests
    var count: Int = 0
}

data class Key(
    val user: User
)

data class State(
    var requests: Requests,
    var offset: Int,
)

val store = UIStore<Key>()
const val MAX_REQUESTS = 5

val RequestsList = component(::RequestsListProps) {
    val scope = object : EventCoroutine {}

    val state = useState(
        State(props.requests, 0)
    )

    store[props.key as Key] = this.ui

    fun update(event: IMessageEditCallback, nextOffset: Int) = scope.launch {
        val hook = event.editMessageEmbeds(
            embed(
                title = "Loading...",
                description = "Fetching data from Database"
            )
        ).queueAsync()

        val next = props.next(nextOffset)

        state update {
            requests = next
            offset = nextOffset
        }

        ui.edit(hook)
    }

    val onNext by onClick { event ->
        update(event, state.value.offset + MAX_REQUESTS)
    }

    val onPrev by onClick { event ->
        val offset = state.value.offset

        update(event, offset - MAX_REQUESTS)
    };

    {
        val (requests, offset) = state.get()
        val max = props.count

        + Content("**Requests** (${offset.coerceAtMost(max)}/$max)")

        + on(requests.isEmpty()) {
            Text()..{
                content = "Not more results"
                type = TextType.CODE_BLOCK
            }
        }

        - requests.map { pair ->
            Item(pair)
        }

        + Row(
            Button.primary(onPrev, "<-", disabled = offset < MAX_REQUESTS),
            Button.primary(onNext, "->", disabled = offset >= max)
        )
    }
}

private val Item = { (request, info): Record2<RequestRecord, RequestInfoRecord> ->
    embed(
        title = info.title,
        description = info.detail?.take(100),
        url = getJumpUrl(request),

        fields = listOf(
            field(
                name = "ID",
                value = request.id.toString(),
                inline = true
            ),

            field(
                name = "State",
                value = RequestState.from(info.state!!).run {
                    "${emoji.formatted} $name"
                },
                inline = true
            ),

            field(
                name = "Requester",
                value = "<@${request.owner}>",
                inline = true
            )
        ),
    )
}