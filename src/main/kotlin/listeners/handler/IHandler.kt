package listeners.handler

import net.dv8tion.jda.api.entities.Guild
import net.dv8tion.jda.api.events.interaction.GenericInteractionCreateEvent
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent

interface IHandler<E: GenericInteractionCreateEvent, D> {
    fun call(event: GenericInteractionCreateEvent, guild: Guild, data: Any) {
        handle(event as E, guild, data as D)
    }

    fun handle(event: E, guild: Guild, data: D)
}

interface IModalHandler<D> : IHandler<ModalInteractionEvent, D>