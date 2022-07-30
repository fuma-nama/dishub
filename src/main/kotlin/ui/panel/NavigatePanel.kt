package ui.panel

import bjda.ui.component.utils.Builder
import bjda.utils.embed
import net.dv8tion.jda.api.interactions.components.ActionRow
import net.dv8tion.jda.api.interactions.components.buttons.Button

val NavigatePanel = { top: String, bottom: String ->

    Builder {
        it.addEmbeds(
            embed(
                title = "Navigate Bar",
                description = "Choose a place to jump to"
            )
        )
        it.addActionRow(
            ActionRow.of(
                Button.link(top, "Top"),
                Button.link(bottom, "Bottom")
            )
        )
    }
}