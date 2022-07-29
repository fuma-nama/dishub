package utils

import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.entities.MessageEmbed
import net.dv8tion.jda.api.interactions.InteractionHook
import net.dv8tion.jda.api.interactions.callbacks.IReplyCallback
import java.awt.Color
import java.util.concurrent.TimeUnit

infix fun IReplyCallback.error(message: MessageEmbed) {
    this.replyEmbeds(message).setEphemeral(true).queue()
}

infix fun IReplyCallback.error(message: String) {
    this.replyEmbeds(buildError(message)).setEphemeral(true).queue()
}

infix fun InteractionHook.error(message: String) {
    editOriginalEmbeds(buildError(message)).queue {
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