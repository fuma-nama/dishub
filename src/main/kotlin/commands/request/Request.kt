package commands.request

import bjda.plugins.supercommand.CommandHandler
import bjda.plugins.supercommand.EventInfo
import bjda.plugins.supercommand.IOptionValue.Companion.choice
import bjda.plugins.supercommand.SuperCommand
import bjda.plugins.supercommand.SuperCommandGroup.Companion.create
import bjda.ui.core.UI
import database.countRequest
import database.getRequestByThread
import database.listRequests
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import listeners.openRequest
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.entities.Member
import net.dv8tion.jda.api.interactions.commands.OptionType
import service.request.DeleteRequestService
import service.GuildSettingsService
import ui.modals.CreateRequestModal
import ui.panel.Key
import ui.panel.RequestsList
import utils.*
import variables.NO_GUILD
import variables.RequestState
import variables.eventThread

val RequestCommands = create(
    "request", "Call Dishub Request Features",
    Open(), Create(), Delete(), List()
)

private class Open : SuperCommand("open", "Open and Subscribe to the Request"), CoroutineScope {
    override val coroutineContext = eventThread

    val request = int("request", "The request Id to open")
        .required(true)

    override val run: CommandHandler = {
        launch {
            openRequest(request(), event)
        }
    }
}

private class List : SuperCommand("list", "List all requests"), EventCoroutine {
    override val coroutineContext = eventThread

    val author = option<Member>(OptionType.USER, "author", "Specially the author of Request")
        .optional()

    val state = text("state", "Specially Requests State")
        .choices(
            RequestState.values().map {
                choice(
                    key = "${it.emoji.formatted} ${it.name}",
                    value = it.name
                )
            }
        )
        .optional()
        .map { it?.let(RequestState::valueOf) }

    override val run = fun EventInfo.() {
        val guild = event.guild
            ?: return event.error(NO_GUILD)
        val author = author()
        val state = state()

        event.later {

            suspend fun next(offset: Int) = listRequests(
                guild.idLong,
                offset = offset,
                author = author?.idLong,
                state = state?.state
            )

            val requests = next(0)
            val count = countRequest(
                guild.idLong,
                author = author?.idLong,
                state = state?.state
            )?: 0

            val ui = UI(
                RequestsList {
                    this.key = Key(event.user)
                    this.requests = requests
                    this.count = count
                    this.next = { offset -> next(offset) }
                }
            )

            ui.edit(it)
        }
    }
}

private class Create : SuperCommand("create", "Create a new Request") {

    override val run = fun EventInfo.() {
        event.guild?: return event.error(NO_GUILD)

        event.replyModal(CreateRequestModal)
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