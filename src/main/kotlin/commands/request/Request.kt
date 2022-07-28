package commands.request

import bjda.plugins.supercommand.EventInfo
import bjda.plugins.supercommand.SuperCommand
import bjda.plugins.supercommand.SuperCommandGroup.Companion.create
import database.getRequestByThread
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.interactions.commands.OptionType
import service.DeleteRequestService
import ui.CreateRequestModal

val RequestCommands = create(
    "request", "Call Dishub Request Features",
    Create(), Delete()
)

fun EventInfo.noGuildError() {
    error("You can only call this command in a Guild")
}

private class Create : SuperCommand("create", "Create a new Request") {

    override val run = fun EventInfo.() {
        event.guild?: return noGuildError()

        event.replyModal(CreateRequestModal.create())
            .queue()
    }
}

private class Delete : SuperCommand("delete", "Delete current request") {
    val id = option<Int>(OptionType.INTEGER, "request", "The request Id")
        .required(false)
        .default { -1 }

    override val run = fun EventInfo.() {
        val guild = event.guild?: return noGuildError()
        var id = id()

        if (id == -1) {
            val request = getRequestByThread(event.channel.idLong)
                ?: return error("You must specially a Request Id or Run this command in Request Thread")

            id = request.id!!
        }

        event.deferReply().setEphemeral(true).queue { hook ->

            DeleteRequestService(guild, id).delete { thread ->
                //Don't edit if the channel has deleted
                if (event.channel.id == thread.id) {
                    return@delete
                }

                val embed = EmbedBuilder().apply {
                    setTitle("Request #$id Deleted")
                    setDescription("Request Data and Channel has been deleted")
                }

                hook.editOriginalEmbeds(
                    embed.build()
                ).queue()
            }
        }
    }
}