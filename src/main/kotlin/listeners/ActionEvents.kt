package listeners

import bjda.ui.core.UIOnce.Companion.buildMessage
import database.fetchRequestFull
import database.fetchRequestInfo
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent
import net.dv8tion.jda.api.events.interaction.component.GenericComponentInteractionCreateEvent
import net.dv8tion.jda.api.events.interaction.component.SelectMenuInteractionEvent
import service.GuildSettingsService
import service.request.UpdateRequestService
import ui.modals.EditRequestModal
import ui.modals.ModifyTagsModal
import ui.modals.get
import ui.modals.value
import ui.panel.RequestUpdatePanel
import ui.panel.StateChangePanel
import utils.*
import variables.NO_GUILD
import variables.RequestState

class ActionEvents: Listener, EventCoroutine {
    override val prefix = ActionEvents.prefix

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
                    val pair = updateRequest(
                        event.value("title"), event.value("detail")
                    )

                    if (pair == null) {
                        hook.error("Failed to edit request")

                        return@later
                    }

                    updateHeader().queueAsync()

                    val ui = RequestUpdatePanel(event.user, pair)

                    hook.editOriginal(ui.buildMessage()).queue()
                }
            }

            Modify_Tags -> event.later { hook ->
                val (request, info) = fetchRequestFull(guild.idLong, id)
                    ?: run {
                        event.error("Request doesn't exists")

                        return@later
                    }
                val tags = parseTags(event["tags"])

                UpdateRequestService(guild, request, info).run update@ {
                    val pair = updateTags(tags)?: run {
                        hook.error("Failed to update Tags")

                        return@update
                    }

                    updateHeader().queueAsync()

                    val ui = RequestUpdatePanel(event.user, pair)

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
            Open_Edit, Open_Modify_Tags -> {

                val info = fetchRequestInfo(guild.idLong, id)
                    ?: return event.error("Request doesn't exists")

                val modal = when (method) {
                    Open_Modify_Tags -> ModifyTagsModal(id, info)
                    else -> EditRequestModal(id, info)
                }

                event.replyModal(modal).queue()
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