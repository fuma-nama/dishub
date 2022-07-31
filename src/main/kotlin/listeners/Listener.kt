package listeners

import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent
import net.dv8tion.jda.api.events.interaction.component.GenericComponentInteractionCreateEvent

interface Listener {
    val prefix: String

    fun onEvent(data: List<String>, event: GenericComponentInteractionCreateEvent)
    fun onEvent(data: List<String>, event: ModalInteractionEvent) = Unit
    fun List<String>.parse(): Pair<String, Int> {
        return this[0] to this[1].toInt()
    }
}