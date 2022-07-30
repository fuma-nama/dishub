package variables

import models.enums.State
import net.dv8tion.jda.api.entities.emoji.Emoji

enum class RequestState(val state: State, val description: String, val emoji: Emoji) {
    Opening(
        State.opening,
        "While a request is opening, it means this request is still discussing or waiting for a reply.",
        Emoji.fromUnicode("U+2705")
    ),
    Processing(
        State.processing,
        "It means the request is accepted and peoples are working on it now.",
        Emoji.fromUnicode("U+1F557")
    ),
    Closed(
        State.closed,
        "If a request is closed, users cannot discuss here until it is reopened.",
        Emoji.fromUnicode("U+1F512")
    );

    companion object {
        fun from(state: State): RequestState {
            return values().find {
                it.state == state
            }!!
        }
    }
}