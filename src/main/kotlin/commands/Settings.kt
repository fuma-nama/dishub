package commands

import bjda.plugins.supercommand.CommandHandler
import bjda.plugins.supercommand.SuperCommand
import bjda.plugins.supercommand.SuperCommandGroup
import bjda.ui.core.UIOnce.Companion.buildMessage
import bjda.ui.core.rangeTo
import net.dv8tion.jda.api.entities.Category
import net.dv8tion.jda.api.entities.ChannelType
import service.GuildSettingsService
import ui.SuccessPanel
import utils.error
import variables.NO_GUILD

val SettingsCommands = SuperCommandGroup.create(
    name = "settings", description = "Manage Settings of this Server",
    Container(), Manager()
)

class Manager : SuperCommand(
    name = "manager", description = "Set the Manager Role"
) {
    val manager = role("role", "The Role to be the manager")
        .required(true)

    override val run: CommandHandler = run@ {
        val role = manager()
        val guild = event.guild
            ?: return@run event.error(NO_GUILD)

        GuildSettingsService(guild).updateManagerRole(role.idLong)

        val ui = SuccessPanel(event.user)..{
            title = "Update Manager Role to ${role.asMention}"
            description = "Peoples with this role can now Manage Requests"
        }

        event.reply(ui.buildMessage())
            .queue()
    }
}


class Container : SuperCommand(
    name = "container", description = "Change the container of requests"
) {
    val container = channel<Category>(
        "container",
        "The channel category to store request threads",
        ChannelType.CATEGORY
    ).required(true)

    override val run: CommandHandler = run@ {
        val guild = event.guild
            ?: return@run event.error(NO_GUILD)
        val container = container()

        GuildSettingsService(guild).updateContainer(container.idLong)

        val ui = SuccessPanel(event.user)..{
            title = "Updated Request Container: ${container.name}"
            description = "Because of Rate Limit, Old threads won't be transferred to new Container"
        }

        event.reply(ui.buildMessage())
            .mention(container)
            .queue()
    }
}
