package listeners.handler.action

import bjda.ui.core.UIOnce.Companion.buildMessage
import bjda.ui.modal.get
import database.fetchRequestFull
import listeners.handler.IModalHandler
import net.dv8tion.jda.api.entities.Guild
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent
import service.request.UpdateRequestService
import ui.panel.RequestUpdatePanel
import utils.*

class RequestEditHandler: IModalHandler<Int>, EventCoroutine {

    override fun handle(event: ModalInteractionEvent, guild: Guild, data: Int) = event.later { hook ->

        val (request, info) = fetchRequestFull(guild.idLong, data)
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
                event["title"], event["detail"]
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
}