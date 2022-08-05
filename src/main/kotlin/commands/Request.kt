package commands

import bjda.plugins.supercommand.IOptionValue.Companion.choice
import bjda.plugins.supercommand.SuperCommandGroup.Companion.create
import bjda.ui.core.UI
import bjda.utils.embed
import database.*
import listeners.handler.request.RequestOpenHandler.Companion.openRequestByDisplayId
import service.request.DeleteRequestService
import service.GuildSettingsService
import service.request.SubscriberService
import ui.modals.CreateRequestModal
import ui.panel.Key
import ui.panel.Requests
import ui.panel.RequestsList
import ui.panel.UnsubscribedPanel
import utils.*
import variables.RequestState

val RequestCommands = create(
    "request", "Call Dishub Request Features",
) {
    command(Open(), Close(), Create(), Delete(), List())
}

fun Open() = dishubCommand("open", "Open and Subscribe to the Request") {
    val scope = EventCoroutine.create()

    val request = long("request", "The request Id to open")
        .required()
        .map { it.toInt() }

    execute {

        scope.laterReply(event, true) { hook ->
            openRequestByDisplayId(request(), event, hook)
        }
    }
}

fun Close() = dishubCommand("close", "Close and Unsubscribe the current request") {
    val scope = EventCoroutine.create()
    val id = long("request", "The ID of request to be closed")
        .optional()
        .map { it?.toInt() }

    execute {
        val displayId = id()
        val guild = event.guild!!

        scope.laterReply(event, true) { hook ->
            val request = if (displayId == null) {
                getRequestByThread(guild.idLong, event.channel.idLong)
            } else {
                getRequest(guild.idLong, displayId)
            }

            request?: run {
                hook.error("Request doesn't exists")

                return@laterReply
            }

            val payload = SubscriberService(guild, request)
                .removeSubscriber(event.member!!)

            if (payload.success) {

                hook.editOriginalEmbeds(
                    UnsubscribedPanel(request.displayId!!)
                ).queue {
                    payload.updater()
                }
            } else {
                hook.error("Failed to Unsubscribe")
            }
        }
    }
}

fun List() = dishubCommand("list", "List all requests") {
    val scope = EventCoroutine.create()

    val search = text("search", "Keyword to search request")
        .optional()

    val author = member( "author", "Specially the author of Request")
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

    execute {
        val guild = event.guild!!

        val filter = Filter.build(
            author = author()?.idLong,
            state = state()?.state,
            keyword = search(),
            subscribedBy = if (onlySubscribed()) {
                event.user.idLong
            } else {
                null
            }
        )

        suspend fun next(offset: Int): Requests {

            return listRequests(
                guild.idLong,
                offset = offset,
                filter = filter
            )
        }

        scope.laterReply(event, true) { hook ->

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

fun Create() = dishubCommand("create", "Create a new Request") {

    execute {
        event.replyModal(CreateRequestModal).queue()
    }
}

fun Delete() = dishubCommand("delete", "Delete current request") {
    val scope = EventCoroutine.create()

    val id = long("request", "The request Id")
        .optional()

    execute {
        val guild = event.guild!!

        scope.laterReply(event, true) { hook ->
            val settings = GuildSettingsService(guild).getOrInit()
            val requestId = id()?.toInt()
                ?: getRequestByThread(guild.idLong, event.channel.idLong)?.id

            if (requestId == null) {
                hook.error("You must specially a Request Id or Run this command in Request Thread")

                return@laterReply
            }

            if (settings.canDeleteRequest(event.member!!)) {
                DeleteRequestService(guild, requestId).delete { thread ->
                    //Don't edit if the channel has deleted
                    if (event.channel.id == thread.id) {
                        return@delete
                    }

                    hook.editOriginalEmbeds(
                        embed(
                            title = "Request #$requestId Deleted",
                            description = "Request Data and Channel has been deleted"
                        )
                    ).queue()
                }
            } else {

                hook.error(noPermissions("delete request"))
            }
        }
    }
}