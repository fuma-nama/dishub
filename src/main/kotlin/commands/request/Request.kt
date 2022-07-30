package commands.request

import bjda.plugins.supercommand.EventInfo
import bjda.plugins.supercommand.SuperCommand
import bjda.plugins.supercommand.SuperCommandGroup.Companion.create
import database.getRequestByThread
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.interactions.commands.OptionType
import service.request.DeleteRequestService
import service.GuildSettingsService
import ui.modals.CreateRequestModal
import utils.*
import variables.NO_GUILD
import variables.eventThread

val RequestCommands = create(
    "request", "Call Dishub Request Features",
    Create(), Delete()
)


private class Create : SuperCommand("create", "Create a new Request") {

    override val run = fun EventInfo.() {
        event.guild?: return event.error(NO_GUILD)

        event.replyModal(CreateRequestModal.create())
            .queue()
    }
}

private class Delete : SuperCommand("delete", "Delete current request"), CoroutineScope {
    override val coroutineContext = eventThread

    val id = option<Long?>(OptionType.INTEGER, "request", "The request Id")
        .required(false)

    override val run = fun EventInfo.() {
        val guild = event.guild?: return event.error(NO_GUILD)

        launch {
            val hook = event.deferReply().setEphemeral(true).queueAsync()
            val settings = GuildSettingsService(guild).getOrInit()
            val id = id()?.toInt()
                ?: getRequestByThread(event.channel.idLong)?.id

            if (id == null) {
                error("You must specially a Request Id or Run this command in Request Thread")

                return@launch
            }

            if (settings.canDeleteRequest(event.member!!)) {
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
            } else {
                val embed = noPermissions("delete request")

                hook.editOriginalEmbeds(embed).queue {
                    it.deleteLater()
                }
            }
        }
    }
}