package listeners.handler.request

import bjda.ui.core.UIOnce.Companion.buildMessage
import database.getRequest
import kotlinx.coroutines.coroutineScope
import listeners.handler.IHandler
import models.tables.records.RequestRecord
import net.dv8tion.jda.api.entities.Guild
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent
import net.dv8tion.jda.api.interactions.InteractionHook
import net.dv8tion.jda.api.interactions.callbacks.IReplyCallback
import service.request.SubscriberService
import ui.panel.SubscribedPanel
import utils.*

class RequestOpenHandler : IHandler<ButtonInteractionEvent, Int>, EventCoroutine {

    companion object {

        private suspend fun openRequest(request: RequestRecord?, event: IReplyCallback, hook: InteractionHook) = coroutineScope run@ {
            val guild = event.guild!!

            if (request == null) {
                hook.error("Request doesn't exists")
            } else {
                SubscriberService(guild, request).addSubscriber(event.member!!)

                val ui = SubscribedPanel(getJumpUrl(request))

                hook.editOriginal(ui.buildMessage()).queue()
            }
        }

        suspend fun openRequest(id: Int, event: IReplyCallback, hook: InteractionHook) {
            val request = getRequest(id)

            return openRequest(request, event, hook)
        }

        suspend fun openRequestByDisplayId(displayId: Int, event: IReplyCallback, hook: InteractionHook) {
            val guild = event.guild!!
            val request = getRequest(guild.idLong, displayId)

            return openRequest(request, event, hook)
        }
    }

    override fun handle(event: ButtonInteractionEvent, guild: Guild, data: Int) = event.later(true) {

        openRequest(data, event, it)
    }
}