package listeners.handler.action

import bjda.ui.core.UIOnce.Companion.buildMessage
import bjda.ui.modal.get
import database.fetchRequestFull
import listeners.handler.IModalHandler
import net.dv8tion.jda.api.entities.Guild
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent
import service.request.UpdateRequestService
import ui.panel.RequestUpdatePanel
import utils.EventCoroutine
import utils.error
import utils.parseTags
import utils.queueAsync

class ModifyTagsHandler : IModalHandler<Int>, EventCoroutine {
    override fun handle(event: ModalInteractionEvent, guild: Guild, data: Int) = event.later { hook ->

        val (request, info) = fetchRequestFull(guild.idLong, data)
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