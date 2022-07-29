package ui.actions

import bjda.ui.component.Embed
import bjda.ui.core.Component
import bjda.ui.core.IProps
import bjda.ui.core.rangeTo
import bjda.ui.types.Children
import net.dv8tion.jda.api.entities.User
import java.awt.Color
import variables.States as RequestState

fun StateChangePanel(state: RequestState, author: User): Embed {

    return Embed()..{
        title = "${state.emoji.formatted} Request State Updated: ${state.name}"
        description = state.description
        color = Color.CYAN

        this.author = author.name
        this.authorIcon = author.avatarUrl
    }
}