package ui.panel

import bjda.ui.component.Embed
import bjda.ui.core.rangeTo
import net.dv8tion.jda.api.entities.User
import java.awt.Color
import variables.RequestState

fun StateChangePanel(state: RequestState, author: User): Embed {

    val description = when (state) {
        RequestState.Opening -> "Request is now opening, you can discuss about this request here"
        RequestState.Processing -> "Request is accepted and processing now"
        RequestState.Closed -> "Request is closed, You cannot discuss here until it is reopened"
    }

    return Embed()..{
        this.title = "${state.emoji.formatted} Request State Updated: ${state.name}"
        this.description = description
        this.color = Color.CYAN

        this.author = author.name
        this.authorIcon = author.avatarUrl
    }
}