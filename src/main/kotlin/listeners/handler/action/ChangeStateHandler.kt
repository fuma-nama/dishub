package listeners.handler.action

import bjda.ui.core.UIOnce.Companion.buildMessage
import database.fetchRequestFull
import listeners.handler.IHandler
import net.dv8tion.jda.api.entities.Guild
import net.dv8tion.jda.api.events.interaction.component.SelectMenuInteractionEvent
import service.GuildSettingsService
import service.request.UpdateRequestService
import ui.panel.StateChangePanel
import utils.*
import variables.RequestState

class ChangeStateHandler: IHandler<SelectMenuInteractionEvent, Int>, EventCoroutine {

    override fun handle(event: SelectMenuInteractionEvent, guild: Guild, data: Int) = event.later { hook ->
        val selected = RequestState.valueOf(event.values[0])

        val (request, info) = fetchRequestFull(guild.idLong, data)
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