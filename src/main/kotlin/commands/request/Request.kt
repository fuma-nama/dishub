package commands.request

import bjda.plugins.supercommand.CommandHandler
import bjda.plugins.supercommand.EventInfo
import bjda.plugins.supercommand.IOptionValue.Companion.choice
import bjda.plugins.supercommand.SuperCommand
import bjda.plugins.supercommand.SuperCommandGroup.Companion.create
import bjda.ui.core.UI
import database.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import listeners.openRequest
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.entities.Member
import net.dv8tion.jda.api.interactions.commands.OptionType
import service.request.DeleteRequestService
import service.GuildSettingsService
import service.request.SubscriberService
import ui.modals.CreateRequestModal
import ui.panel.Key
import ui.panel.Requests
import ui.panel.RequestsList
import ui.panel.UnsubscribedPanel
import utils.*
import variables.NO_GUILD
import variables.RequestState
import variables.eventThread

val RequestCommands = create(
    "request", "Call Dishub Request Features",
    Open(), Close(), Create(), Delete(), List()
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

private class Close : SuperCommand("close", "Close and Unsubscribe the current request"), EventCoroutine {
    val request = int("request", "The ID of request to be closed")
        .optional()

    override val run: CommandHandler = run@ {
        val id = request()
        val guild = event.guild
            ?: return@run event.error(NO_GUILD)

        event.later(true) { hook ->
            val request = if (id == null) {
                getRequestByThread(guild.idLong, event.channel.idLong)
            } else {
                getRequest(guild.idLong, id)
            }

            request?: run {
                hook.error("Request doesn't exists")

                return@later
            }

            val payload = SubscriberService(guild, request)
                .removeSubscriber(event.member!!)

            if (payload.success) {

                hook.editOriginalEmbeds(
                    UnsubscribedPanel(request.id!!)
                ).queue {
                    payload.updater()
                }
            } else {
                hook.error("Failed to Unsubscribe")
            }
        }
    }
}

private class List : SuperCommand("list", "List all requests"), EventCoroutine {
    val search = text("search", "Keyword to search request")
        .optional()

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

    val onlySubscribed = boolean("subscribed", "Only list subscribed Requests")
        .optional { false }

    override val run = fun EventInfo.() {
        val guild = event.guild
            ?: return event.error(NO_GUILD)
        val author = author()
        val state = state()

        val filter = Filter.build(
            author = author?.idLong,
            state = state?.state,
            keyword = search(),
            subscribedBy = if (onlySubscribed()) {
                event.user.idLong
            } else {
                null
            }
        )

        event.later(true) { hook ->

            suspend fun next(offset: Int): Requests {

                return listRequests(
                    guild.idLong,
                    offset = offset,
                    filter = filter
                )
            }

            val requests = next(0)
            val count = countRequest(
                guild.idLong,
                filter = filter
            )

            val ui = UI(
                RequestsList {
                    this.key = Key(event.user)
                    this.requests = requests
                    this.count = count?: 0
                    this.next = { offset -> next(offset) }
                }
            )

            ui.edit(hook)
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
                ?: getRequestByThread(guild.idLong, event.channel.idLong)?.id

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