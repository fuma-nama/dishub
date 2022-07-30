package ui

import bjda.ui.component.Embed
import bjda.ui.core.rangeTo
import net.dv8tion.jda.api.entities.User
import java.awt.Color

val SuccessPanel = {user: User ->
    Embed()..{
        color = Color.CYAN
        author = user.name
        authorIcon = user.avatarUrl
    }
}