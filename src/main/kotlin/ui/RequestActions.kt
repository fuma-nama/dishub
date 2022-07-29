package ui

import bjda.plugins.ui.hook.ButtonClick.Companion.onClick
import bjda.plugins.ui.hook.MenuSelect.Companion.onSelect
import bjda.plugins.ui.modal.Form.Companion.value
import bjda.ui.component.Embed
import bjda.ui.component.RowLayout
import bjda.ui.component.action.Button
import bjda.ui.component.action.Menu
import bjda.ui.core.*
import bjda.ui.types.Children
import kotlinx.coroutines.*
import models.tables.records.GuildRecord
import variables.States as RequestState
import models.tables.records.RequestInfoRecord
import models.tables.records.RequestRecord
import net.dv8tion.jda.api.entities.Guild
import net.dv8tion.jda.api.entities.Member
import net.dv8tion.jda.api.interactions.components.buttons.ButtonStyle
import net.dv8tion.jda.api.interactions.components.selections.SelectOption
import service.request.UpdateRequestService
import ui.actions.StateChangePanel
import ui.modals.EditRequestModal
import utils.*
import variables.eventThread
import java.awt.Color
import kotlin.properties.Delegates

class RequestActions(val owner: Member, val guild: Guild) : Component<RequestActions.Props>(Props()), CoroutineScope {
    override val coroutineContext = eventThread
    private var canModifyState by Delegates.notNull<Boolean>()
    private var canEdit by Delegates.notNull<Boolean>()
    lateinit var service: UpdateRequestService

    class Props : IProps() {
        lateinit var request: RequestRecord
        lateinit var info: RequestInfoRecord
        lateinit var config: GuildRecord
    }

    override fun onMount() {
        with (props) {
            canModifyState = config.canModifyState(owner)
            canEdit = request.canEditRequest(owner)
            service = UpdateRequestService(guild, request, info)
        }
    }

    private val editModal = EditRequestModal { event ->
        if (canEdit) launch {
            val hook = event.deferReply().queueAsync()
            val success = service.updateRequest(
                event.value("title"), event.value("detail")
            )

            if (success) {
                service.updateHeader().queueAsync()

                val ui = UIOnce(
                    SuccessPanel(owner.user)..{
                        title = "Edited the Request"
                        fields = fields(
                            field("Old Title" to props.info.title!!, true),
                            field("New Title" to service.info.title!!, true),
                            field("" to "", false),
                            field("Old Detail" to props.info.detail!!, true),
                            field("New Detail" to service.info.detail!!, true)
                        )
                    }
                )

                hook.editOriginal(ui.get()).queue()
            } else {
                hook.error("Failed to edit request")
            }
        } else {
            event.error(
                noPermissions("edit request")
            )
        }
    }

    private val onEdit by onClick { event ->
        event.replyModal(editModal.create()).queue()
    }

    private val onChangeState by onSelect { event ->
        val selected = RequestState.valueOf(event.values[0])

        if (canModifyState) launch {
            val hook = event.deferReply().queueAsync()

            service.run {
                if (updateState(selected.state)) {
                    updateHeader().queueAsync()
                } else {
                    return@launch hook.error("Failed to update State")
                }
            }

            val ui = UIOnce(
                StateChangePanel(selected, event.user)
            )

            hook.editOriginal(ui.get()).queue()
        } else {
            event.error(noPermissions("change request state"))
        }
    }

    override fun onRender(): Children {

        return {
            + Embed()..{
                title = "Actions"
                color = Color.GREEN
            }

            + RowLayout()-{
                if (canModifyState)
                    + Menu(onChangeState) {
                        placeholder = "Change Request state"
                        options = RequestState.values().map {
                            Option(it, it.state == props.info.state)
                        }
                    }

                if (canEdit)
                    + Button(onEdit) {
                        label = "Edit"
                        style = ButtonStyle.SECONDARY
                    }
            }
        }
    }

    class Option(state: RequestState, selected: Boolean) : SelectOption(
        state.name, state.name,
        state.description,
        selected,
        state.emoji
    )

    data class Key(val guild: Long, val request: Int, val user: Long)
}