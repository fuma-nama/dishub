package commands.thread

import bjda.plugins.supercommand.CommandHandler
import bjda.plugins.supercommand.SuperCommand
import bjda.plugins.supercommand.SuperCommandGroup
import bjda.ui.core.UIOnce.Companion.buildMessage
import database.fetchRequestFull
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import ui.panel.NavigatePanel
import ui.panel.ReferencePanel
import utils.error
import utils.queueAsync
import variables.NO_GUILD
import variables.eventThread

val ThreadCommands = SuperCommandGroup.create(
    name = "thread", description = "Commands used in a request thread",
    Navigate(), Reference()
)

private class Reference: SuperCommand(name = "reference", description = "mention another request thread"), CoroutineScope {
    override val coroutineContext = eventThread

    val request = int("request", "The Request Id to reference")
        .required(true)

    override val run: CommandHandler = run@ {
        val requestId = request()
        val guild = event.guild
            ?: return@run event.error(NO_GUILD)

        event.deferReply().queue { hook ->
            launch {
                val (request, info) = fetchRequestFull(guild.idLong, requestId)
                    ?: return@launch hook.error("Request doesn't exists")

                hook.editOriginal(
                    ReferencePanel(event.member!!, request, info)
                        .buildMessage()
                ).queue()
            }
        }
    }
}

private class Navigate: SuperCommand(name = "nav", description = "A navigate bar to jump to header or bottom"), CoroutineScope {
    override val coroutineContext = eventThread

    override val run: CommandHandler = {

        event.deferReply(true).queue { hook ->
            launch {

                val first = event.channel.getHistoryFromBeginning(1)
                    .queueAsync()
                    .retrievedHistory
                    .firstOrNull()

                val last = event.channel.history.retrievePast(1)
                    .queueAsync()
                    .firstOrNull()

                if (first == null || last == null) {
                    hook.error("Message Not Found")
                } else {

                    val ui = NavigatePanel(first.jumpUrl, last.jumpUrl).buildMessage()

                    hook.editOriginal(ui).queue()
                }
            }
        }
    }
}