package listeners.handler.request

import bjda.ui.core.UIOnce.Companion.buildMessage
import database.getRequest
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import listeners.handler.IHandler
import net.dv8tion.jda.api.entities.Guild
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent
import net.dv8tion.jda.api.events.interaction.component.GenericComponentInteractionCreateEvent
import net.dv8tion.jda.api.interactions.InteractionHook
import net.dv8tion.jda.api.interactions.callbacks.IReplyCallback
import service.request.SubscriberService
import ui.panel.SubscribedPanel
import utils.*
import variables.NO_GUILD

class RequestOpenHandler : IHandler<ButtonInteractionEvent, Int>, EventCoroutine {

    companion object {

        suspend fun openRequest(id: Int, event: IReplyCallback, hook: InteractionHook) = coroutineScope run@ {
            val guild = event.guild!!
            val request = getRequest(guild.idLong, id)

            if (request == null) {
                hook.error("Request doesn't exists")
            } else {
                SubscriberService(guild, request).addSubscriber(event.member!!)

                val ui = SubscribedPanel(getJumpUrl(request))

                hook.editOriginal(ui.buildMessage()).queue()
            }
        }
    }

    override fun handle(event: ButtonInteractionEvent, guild: Guild, data: Int) = event.later(true) {

        openRequest(data, event, it)
    }
}