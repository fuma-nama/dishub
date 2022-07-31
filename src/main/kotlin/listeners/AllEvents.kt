package listeners

import kotlinx.coroutines.CoroutineScope
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent
import net.dv8tion.jda.api.events.interaction.component.GenericComponentInteractionCreateEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import variables.eventThread

class AllEvents: ListenerAdapter(), CoroutineScope {
    override val coroutineContext = eventThread
    val listeners = listOf<Listener>(
        ActionEvents(), RequestEvents()
    ).associateBy { it.prefix }

    override fun onGenericComponentInteractionCreate(event: GenericComponentInteractionCreateEvent) {
        val id = event.componentId
        val args = id.split('-')
        val prefix = args[0]

        listeners[prefix]?.onEvent(args.drop(1), event)
    }

    override fun onModalInteraction(event: ModalInteractionEvent) {
        val id = event.modalId
        val args = id.split('-')
        val prefix = args[0]

        listeners[prefix]?.onEvent(args.drop(1), event)
    }
}

class Methods {
    companion object {
        fun request(method: String, vararg args: Any): String {
            val arg = args.joinToString(separator = "-")

            return "${RequestEvents.prefix}-$method-$arg"
        }

        fun action(method: String, vararg args: Any): String {
            val arg = args.joinToString(separator = "-")

            return "${ActionEvents.prefix}-$method-$arg"
        }
    }
}