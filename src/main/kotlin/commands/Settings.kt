package commands

import bjda.plugins.supercommand.CommandHandler
import bjda.plugins.supercommand.SuperCommand
import bjda.plugins.supercommand.SuperCommandGroup
import service.GuildSettingsService
import utils.error
import variables.NO_GUILD

val Settings = SuperCommandGroup.create(
    name = "settings", description = "Manage Settings of this Server",
    Container()
)

class Container : SuperCommand(
    name = "container", description = "Change the container of requests"
) {
    override val run: CommandHandler = run@ {
        val guild = event.guild
            ?: return@run event.error(NO_GUILD)

        GuildSettingsService(guild)
        event
    }
}
