package utils

import bjda.ui.core.apply
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.entities.MessageEmbed
import java.awt.Color

fun noPermissions(action: String, required: String = "Admin and Manager"): MessageEmbed {
    val embed = EmbedBuilder().apply {
        setTitle("You don't have the Permission to $action")
        setDescription("Only $required can do this action")
        setColor(Color.RED)
    }

    return embed.build()
}