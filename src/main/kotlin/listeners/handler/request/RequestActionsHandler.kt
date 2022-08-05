package listeners.handler.request

import bjda.ui.core.UIOnce.Companion.buildMessage
import database.fetchRequestFull
import listeners.handler.IHandler
import net.dv8tion.jda.api.entities.Guild
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent
import service.GuildSettingsService
import ui.RequestActions
import utils.*

class RequestActionsHandler : IHandler<ButtonInteractionEvent, Int>, EventCoroutine {

    override fun handle(event: ButtonInteractionEvent, guild: Guild, data: Int) = event.later(true) { hook ->

        val record = fetchRequestFull(guild.idLong, data)
        val config = GuildSettingsService(guild).getOrInit()

        if (record == null) {
            event.error("Request doesn't exists")
        } else {
            val (request, info) = record

            val ui = RequestActions {
                this.owner = event.member!!
                this.guild = guild
                this.request = request
                this.info = info
                this.config = config
            }

            hook.editOriginal(ui.buildMessage()).queue()
        }
    }
}