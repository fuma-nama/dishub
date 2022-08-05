package listeners

import listeners.handler.IHandler
import listeners.handler.request.RequestActionsHandler
import listeners.handler.request.RequestCreateHandler
import listeners.handler.request.RequestOpenHandler
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent
import net.dv8tion.jda.api.events.interaction.component.GenericComponentInteractionCreateEvent
import utils.*
import variables.NO_GUILD

class RequestEvents: Listener, EventCoroutine {
    override val prefix = RequestEvents.prefix

    private val handlers = mapOf<String, IHandler<*, Int>>(
        Actions to RequestActionsHandler(),
        Subscribe to RequestOpenHandler()
    )
    private val modalHandlers = mapOf(
        Create to RequestCreateHandler()
    )

    override fun onEvent(data: List<String>, event: ModalInteractionEvent) {
        val (method) = data
        val guild = event.guild?: return event.error(NO_GUILD)

        modalHandlers[method]?.handle(event, guild, Unit)
    }

    override fun onEvent(data: List<String>, event: GenericComponentInteractionCreateEvent) {
        val (method, id) = data.parse()
        val guild = event.guild?: return event.error(NO_GUILD)

        handlers[method]?.call(event, guild, id)
    }

    companion object {
        const val prefix = "__request"
        const val Actions = "actions"
        const val Create = "create"
        const val Subscribe = "subscribe"
    }
}