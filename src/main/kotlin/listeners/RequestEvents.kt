package listeners

import bjda.ui.core.UI
import net.dv8tion.jda.api.events.interaction.component.GenericComponentInteractionCreateEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import ui.RequestActions

class Methods {
    companion object {
        const val Modify = "modify"

        fun build(method: String, vararg args: Any): String {
            val arg = args.joinToString(separator = "-")

            return "${RequestEvents.prefix}$method-$arg"
        }
    }
}

class RequestEvents: ListenerAdapter() {
    companion object {
        const val prefix = "__request_"
    }

    override fun onGenericComponentInteractionCreate(event: GenericComponentInteractionCreateEvent) {
        val id = event.componentId

        if (id.startsWith(prefix)) {

            onEvent(id.removePrefix(prefix), event)
        }
    }

    fun onEvent(id: String, event: GenericComponentInteractionCreateEvent) {
        val (method, request) = id.split('-')

        when (method) {
            Methods.Modify -> {
                UI(
                    RequestActions()
                ).reply(event, true)
            }
        }
    }
}