package ui.panel

import bjda.plugins.ui.hook.ButtonClick.Companion.onClick
import bjda.ui.component.Content
import bjda.ui.component.Embed.Companion.toComponent
import bjda.ui.component.action.Button
import bjda.ui.component.row.Row
import bjda.ui.core.FComponent.Companion.component
import bjda.ui.core.IProps
import bjda.utils.embed
import bjda.utils.field
import kotlinx.coroutines.launch
import models.tables.records.RequestInfoRecord
import models.tables.records.RequestRecord
import net.dv8tion.jda.api.interactions.callbacks.IMessageEditCallback
import org.jooq.Record2
import org.jooq.Result
import utils.EventCoroutine
import utils.getJumpUrl
import utils.queueAsync
import variables.RequestState

typealias Requests = Result<Record2<RequestRecord, RequestInfoRecord>>

class RequestsListProps : IProps() {
    lateinit var requests: Requests
    lateinit var next: suspend (offset: Int) -> Requests
    var count: Int = 0
}

class State(
    var requests: Requests,
    var offset: Int,
)

val RequestsList = component(::RequestsListProps) {
    val maxEmbed = 10
    val scope = object : EventCoroutine {}

    val state = useState(
        State(props.requests, 0)
    )

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
        update(event, state.value.offset + maxEmbed)
    }

    val onPrev by onClick { event ->
        val offset = state.value.offset

        if (offset < maxEmbed) {
            ui.edit(event)
        } else {
            update(event, offset - maxEmbed)
        }
    };

    {
        + Content("**Requests** (${state.get().offset}/${props.count})")

        + props.requests.map {(request, info) ->
            embed(
                title = info.title,
                description = info.detail?.take(100),
                url = getJumpUrl(request),

                fields = arrayListOf(
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
                        value = "<@${request.owner}>"
                    )
                ),
            ).toComponent()
        }

        with (state.get()) {
            + Row(
                Button.primary(onPrev, "<-", disabled = offset > maxEmbed),
                Button.primary(onNext, "->")
            )
        }
    }
}