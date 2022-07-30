package listeners

import bjda.ui.core.UI
import bjda.ui.core.rangeTo
import bjda.ui.utils.UIStore
import database.fetchRequestFull
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import net.dv8tion.jda.api.events.interaction.component.GenericComponentInteractionCreateEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import service.GuildSettingsService
import ui.RequestActions
import utils.error
import utils.queueAsync
import variables.NO_GUILD
import variables.eventThread

val actionsStore = UIStore<RequestActions.Key>()

class Methods {
    companion object {
        const val Actions = "actions"
        const val Delete = "delete"

        fun build(method: String, vararg args: Any): String {
            val arg = args.joinToString(separator = "-")

            return "${RequestEvents.prefix}$method-$arg"
        }
    }
}

class RequestEvents: ListenerAdapter(), CoroutineScope {
    override val coroutineContext = eventThread

    companion object {
        const val prefix = "__request_"
    }

    override fun onGenericComponentInteractionCreate(event: GenericComponentInteractionCreateEvent) {
        val id = event.componentId

        if (id.startsWith(prefix)) {

            onEvent(id.removePrefix(prefix), event)
        }
    }

    fun onEvent(data: String, event: GenericComponentInteractionCreateEvent) {
        val (method, requestId) = data.split('-')
        val id = requestId.toInt()
        val guild = event.guild?: return event.error(NO_GUILD)

        when (method) {
            Methods.Actions -> {
                val key = RequestActions.Key(guild.idLong, id, event.user.idLong)
                val ui = actionsStore[key]

                if (ui != null) {
                    return ui.reply(event)
                }

                launch {
                    val hook = event.deferReply(true).queueAsync()
                    val record = fetchRequestFull(guild.idLong, id)
                    val config = GuildSettingsService(guild).getOrInit()

                    if (record == null) {
                        event.error("Request doesn't exists")
                    } else {
                        val (request, info) = record

                        UI(
                            RequestActions(event.member!!, guild)..{
                                this.request = request
                                this.info = info
                                this.config = config
                            }
                        ).edit(hook)
                    }
                }
            }
        }
    }
}