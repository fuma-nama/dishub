package ui.panel

import bjda.ui.component.utils.Builder
import bjda.utils.embed
import net.dv8tion.jda.api.interactions.components.ActionRow
import net.dv8tion.jda.api.interactions.components.buttons.Button

fun SubscribedPanel(url: String): Builder {
    return Builder {
        it.addEmbeds(
            embed(
                title = "Subscribed to the Thread",
                description = "You can see and open the request now!",
                url = url
            )
        )

        it.addActionRow(
            ActionRow.of(
                Button.link(url, "Open")
            )
        )
    }
}