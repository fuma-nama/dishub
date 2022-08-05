package utils

import bjda.plugins.supercommand.SuperCommand
import bjda.plugins.supercommand.SuperCommandBuilder
import bjda.plugins.supercommand.SuperCommandImpl
import bjda.plugins.supercommand.command
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

fun parseTags(tags: String?): Array<String>? {
    if (tags == null || tags.isEmpty())
        return null

    return tags.split(" ").toTypedArray()
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

fun dishubCommand(
    name: String,
    description: String,
    init: SuperCommandBuilder.() -> Unit
): SuperCommand {
    val cmdImpl = command(name, description, true, init = init) as SuperCommandImpl

    return cmdImpl.apply {
        val override = this.run

        run = run@ {
            event.verify {
                return@run
            }

            override(this)
        }
    }
}