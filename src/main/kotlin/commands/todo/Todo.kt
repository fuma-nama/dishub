package commands.todo

import bjda.plugins.supercommand.CommandHandler
import bjda.plugins.supercommand.SuperCommand
import bjda.plugins.supercommand.SuperCommandGroup
import bjda.ui.core.*
import bjda.ui.utils.UIStore
import commands.todo.getTranslation
import database.getTodosAsync
import net.dv8tion.jda.api.entities.User
import net.dv8tion.jda.api.interactions.callbacks.IReplyCallback
import ui.TodoApp

val TodoCommands = SuperCommandGroup.create(
    "todo", "Todo Commands",
    CreateTodo()
)

val todoStore = UIStore<User>()

private class CreateTodo: SuperCommand(name = "create", description = "Create a Todo List") {
    override val run: CommandHandler = {
        val language = getTranslation(event.userLocale)

        event.replyAsync(todoStore) { update ->
            getTodosAsync(event.user.idLong) {
                val ui = UI(
                    TodoApp(it, language)..{
                        owner = event.user
                    }
                )

                update(ui)
            }
        }
    }
}

fun<T: IReplyCallback> T.replyAsync(store: UIStore<User>, execute: (update: (UI) -> Unit) -> Unit) {
    val ui = store[user]

    if (ui == null) {

        deferReply().queue {
            execute {ui ->
                store[user] = ui

                ui.edit(hook) {
                    ui.listen(it)
                }
            }
        }
    } else {
        ui.reply(this) {
            ui.listen(it)
        }
    }
}