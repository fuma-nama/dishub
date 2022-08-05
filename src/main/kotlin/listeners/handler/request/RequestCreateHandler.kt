package listeners.handler.request

import bjda.ui.component.action.Button
import bjda.ui.modal.get
import bjda.utils.message
import listeners.handler.IModalHandler
import net.dv8tion.jda.api.entities.Guild
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent
import service.request.CreateRequestService
import service.request.RequestOption
import utils.EventCoroutine
import utils.getJumpUrl
import utils.parseTags
import java.awt.Color

class RequestCreateHandler : IModalHandler<Any>, EventCoroutine {

    override fun handle(event: ModalInteractionEvent, guild: Guild, data: Any) = event.later(true) { hook ->

        val info = RequestOption(
            event["title"],
            event["detail"],
            event.user,
            parseTags(event["tags"])
        )

        CreateRequestService(guild).create(info) { request ->
            val ui = message {
                embed(
                    title = "Request Created: #${request.id}",
                    description = "You may open request thread by the button below",
                    color = Color.GREEN
                )

                row (
                    Button.link(
                        label = "Open Thread",
                        url = getJumpUrl(request)
                    )
                )
            }

            hook.editOriginal(ui.build()).queue()
        }
    }
}