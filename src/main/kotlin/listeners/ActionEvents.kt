package listeners

import bjda.plugins.ui.modal.Form.Companion.value
import bjda.ui.core.UIOnce.Companion.buildMessage
import bjda.ui.core.rangeTo
import bjda.utils.blank
import bjda.utils.field
import database.fetchRequestFull
import database.fetchRequestInfo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent
import net.dv8tion.jda.api.events.interaction.component.GenericComponentInteractionCreateEvent
import net.dv8tion.jda.api.events.interaction.component.SelectMenuInteractionEvent
import net.dv8tion.jda.api.interactions.InteractionHook
import net.dv8tion.jda.api.interactions.callbacks.IReplyCallback
import service.GuildSettingsService
import service.request.UpdateRequestService
import ui.SuccessPanel
import ui.modals.EditRequestModal
import ui.panel.StateChangePanel
import utils.*
import variables.NO_GUILD
import variables.RequestState
import variables.eventThread

class ActionEvents: Listener, CoroutineScope {
    override val coroutineContext = eventThread
    override val prefix = ActionEvents.prefix

    fun IReplyCallback.later(ephemeral: Boolean = false, block: suspend CoroutineScope.(InteractionHook) -> Unit) {
        deferReply(ephemeral).queue { hook ->
            launch { block(hook) }
        }
    }

    override fun onEvent(data: List<String>, event: ModalInteractionEvent) {
        val guild = event.guild?: return event.error(NO_GUILD)
        val (method, id) = data.parse()

        when (method) {
            Edit -> event.later { hook ->
                val (request, info) = fetchRequestFull(guild.idLong, id)
                    ?: run {
                        event.error("Request doesn't exists")

                        return@later
                    }

                if (!request.canEditRequest(event.member!!)) {

                    return@later event.error(
                        noPermissions("edit request", "Author")
                    )
                }

                UpdateRequestService(guild, request, info).run {
                    val (old, new) = updateRequest(
                        event.value("title"), event.value("detail")
                    )?: run {
                        hook.error("Failed to edit request")

                        return@later
                    }

                    updateHeader().queueAsync()

                    val ui = SuccessPanel(event.user)..{
                        title = "Edited the Request"
                        fields = listOf(
                            field("Old Title", old.title!!, true),
                            field("New Title", new.title!!, true),
                            blank(),
                            field("Old Detail", old.detail!!, true),
                            field("New Detail", new.detail!!, true)
                        )
                    }

                    hook.editOriginal(ui.buildMessage()).queue()
                }
            }
        }
    }

    override fun onEvent(data: List<String>, event: GenericComponentInteractionCreateEvent) {
        val guild = event.guild?: return event.error(NO_GUILD)
        val (method, id) = data.parse()

        when (method) {
            Change_State -> {
                event as SelectMenuInteractionEvent
                val selected = RequestState.valueOf(event.values[0])

                event.later { hook ->
                    val (request, info) = fetchRequestFull(guild.idLong, id)
                        ?: run {
                            event.error("Request doesn't exists")

                            return@later
                        }

                    val service = UpdateRequestService(guild, request, info)
                    val config = GuildSettingsService(guild).getOrInit()

                    if (!config.canModifyState(guild, event.member!!)) {
                        hook.error(noPermissions("change request state"))

                        return@later
                    }

                    if (selected.state != info.state) {

                        service.run {
                            if (updateState(selected.state)) {

                                (updateHeader() + updatePermissions()).queueAsync()
                            } else {
                                return@later hook.error("Failed to update State")
                            }
                        }
                    }

                    val ui = StateChangePanel(selected, event.user)

                    hook.editOriginal(ui.buildMessage()).queue()
                }
            }
            Open_Edit -> {
                val info = fetchRequestInfo(guild.idLong, id)
                    ?: return event.error("Request doesn't exists")

                val modal = EditRequestModal(id, info)
                event.replyModal(modal).queue()
            }
        }
    }

    companion object {
        const val prefix = "__actions"
        const val Change_State = "change_state"
        const val Open_Edit = "open_edit"
        const val Edit = "edit"
    }
}