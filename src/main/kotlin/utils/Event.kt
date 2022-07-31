package utils

import bjda.plugins.ui.hook.ButtonClick
import bjda.plugins.ui.hook.MenuSelect
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.entities.MessageEmbed
import net.dv8tion.jda.api.interactions.InteractionHook
import net.dv8tion.jda.api.interactions.callbacks.IReplyCallback
import net.dv8tion.jda.api.interactions.components.buttons.ButtonInteraction
import net.dv8tion.jda.api.interactions.components.selections.SelectMenuInteraction
import java.awt.Color
import java.util.concurrent.TimeUnit

infix fun IReplyCallback.error(message: MessageEmbed) {
    this.replyEmbeds(message).setEphemeral(true).queue()
}

infix fun IReplyCallback.error(message: String) {
    this.replyEmbeds(buildError(message)).setEphemeral(true).queue()
}

infix fun InteractionHook.error(message: String) {
    error(buildError(message))
}

infix fun InteractionHook.error(message: MessageEmbed) {
    editOriginalEmbeds(message).queue {
        it.delete().queueAfter(3, TimeUnit.SECONDS)
    }
}

fun buildError(message: String): MessageEmbed {
    val embed = EmbedBuilder().apply {
        setTitle("Error")
        setDescription(message)
        setColor(Color.RED)
    }

    return embed.build()
}

fun onSelectStatic(id: String, handler: (event: SelectMenuInteraction) -> Unit): String {
    MenuSelect(id, handler).listen()
    return id
}

fun onClickStatic(id: String, handler: (event: ButtonInteraction) -> Unit): String {
    ButtonClick(id, handler).listen()
    return id
}