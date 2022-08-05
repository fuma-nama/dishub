package listeners

import database.fetchRequestInfo
import listeners.handler.IModalHandler
import listeners.handler.action.ChangeStateHandler
import listeners.handler.action.ModifyTagsHandler
import listeners.handler.action.RequestEditHandler
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent
import net.dv8tion.jda.api.events.interaction.component.GenericComponentInteractionCreateEvent
import ui.modals.EditRequestModal
import ui.modals.ModifyTagsModal
import utils.*
import variables.NO_GUILD

class ActionEvents: Listener, EventCoroutine {
    override val prefix = ActionEvents.prefix
    private val handlers = mapOf(
        Change_State to ChangeStateHandler(),
    )

    private val modalHandlers = mapOf<String, IModalHandler<*>>(
        Edit to RequestEditHandler(),
        Modify_Tags to ModifyTagsHandler()
    )

    override fun onEvent(data: List<String>, event: ModalInteractionEvent) {
        val guild = event.guild?: return event.error(NO_GUILD)
        val (method, id) = data.parse()

        modalHandlers[method]?.call(event, guild, id)
    }

    override fun onEvent(data: List<String>, event: GenericComponentInteractionCreateEvent) {
        val guild = event.guild?: return event.error(NO_GUILD)
        val (method, id) = data.parse()

        when (method) {
            Open_Edit, Open_Modify_Tags -> {

                val info = fetchRequestInfo(guild.idLong, id)
                    ?: return event.error("Request doesn't exists")

                val modal = when (method) {
                    Open_Modify_Tags -> ModifyTagsModal(id, info)
                    else -> EditRequestModal(id, info)
                }

                event.replyModal(modal).queue()
            }

            else -> {
                handlers[method]?.call(event, guild, id)
            }
        }
    }

    companion object {
        const val prefix = "__actions"
        const val Change_State = "change_state"
        const val Open_Edit = "open_edit"
        const val Edit = "edit"
        const val Open_Modify_Tags = "open_modify_tags"
        const val Modify_Tags = "modify_tags"
    }
}