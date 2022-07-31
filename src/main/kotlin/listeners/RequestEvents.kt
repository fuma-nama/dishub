package listeners

import bjda.ui.core.UIOnce.Companion.buildMessage
import database.fetchRequestFull
import database.getRequest
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import net.dv8tion.jda.api.events.interaction.component.GenericComponentInteractionCreateEvent
import net.dv8tion.jda.api.interactions.callbacks.IReplyCallback
import service.GuildSettingsService
import service.request.SubscriberService
import ui.RequestActions
import ui.panel.SubscribedPanel
import utils.error
import utils.getJumpUrl
import utils.queueAsync
import variables.NO_GUILD
import variables.eventThread

suspend fun<T: IReplyCallback> openRequest(id: Int, event: T) = coroutineScope run@ {
    val guild = event.guild
        ?: return@run event.error(NO_GUILD)

    val hook = event.deferReply(true).queueAsync()
    val request = getRequest(guild.idLong, id)

    if (request == null) {
        event.error("Request doesn't exists")
    } else {
        SubscriberService(guild, request).addSubscriber(event.member!!)

        val ui = SubscribedPanel(getJumpUrl(request))

        hook.editOriginal(ui.buildMessage()).queue()
    }
}

class RequestEvents: Listener, CoroutineScope {
    override val coroutineContext = eventThread
    override val prefix = RequestEvents.prefix

    override fun onEvent(data: List<String>, event: GenericComponentInteractionCreateEvent) {
        val (method, id) = data.parse()
        val guild = event.guild?: return event.error(NO_GUILD)

        when (method) {
            Subscribe -> launch {
                openRequest(id, event)
            }

            Actions -> launch {
                val hook = event.deferReply(true).queueAsync()
                val record = fetchRequestFull(guild.idLong, id)
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
    }

    companion object {
        const val prefix = "__request"
        const val Actions = "actions"
        const val Subscribe = "subscribe"
    }
}