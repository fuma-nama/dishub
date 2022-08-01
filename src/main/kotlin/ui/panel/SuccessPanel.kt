package ui

import bjda.ui.component.Embed
import bjda.ui.core.rangeTo
import bjda.utils.author
import net.dv8tion.jda.api.entities.User
import java.awt.Color

val SuccessPanel = {user: User ->
    Embed()..{
        color = Color.CYAN
        author = author(
            name = user.name,
            icon = user.avatarUrl
        )
    }
}