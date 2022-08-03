package commands.todo

import bjda.plugins.supercommand.SuperCommandGroup
import bjda.plugins.supercommand.command
import bjda.ui.core.*
import database.getTodosAsync
import net.dv8tion.jda.api.entities.User
import ui.TodoApp
import utils.EventCoroutine
import utils.UIStore

val TodoCommands = SuperCommandGroup.create(
    "todo", "Todo Commands"
) {
    command(
        createTodo()
    )
}

val todoStore = UIStore<User>()

fun createTodo() = command(name = "create", description = "Create a Todo List") {
    val scope = EventCoroutine.create()

    execute {
        val language = getTranslation(event.userLocale)

        scope.laterReply(event) { hook ->

            val todos = getTodosAsync(event.user.idLong)
                ?.filterNotNull()
                ?.toMutableList()

            val ui = UI(
                TodoApp(todos?: ArrayList(), language)..{
                    owner = event.user
                }
            )

            todoStore[event.user] = ui

            ui.edit(hook) {
                ui.listen(hook)
            }
        }
    }
}