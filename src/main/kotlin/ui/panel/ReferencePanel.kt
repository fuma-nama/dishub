package ui.panel

import bjda.ui.component.utils.Builder
import bjda.utils.author
import bjda.utils.embed
import models.tables.records.RequestInfoRecord
import models.tables.records.RequestRecord
import net.dv8tion.jda.api.entities.Member
import net.dv8tion.jda.api.interactions.components.ActionRow
import net.dv8tion.jda.api.interactions.components.buttons.Button

val ReferencePanel = { author: Member, request: RequestRecord, info: RequestInfoRecord ->
    val name = author.nickname?: author.user.name
    val avatar = author.avatarUrl?: author.user.avatarUrl

    Builder {
        it.addEmbeds(
            embed(
                title = "$name Mentioned Request: #${info.request}",
                description = info.title,
                author = author(
                    name = name,
                    icon = avatar
                )
            )
        )

        it.addActionRow(
            ActionRow.of(
                Button.link(
                    "https://discord.com/channels/${request.guild}/${request.thread}",
                    "Open"
                )
            )
        )
    }
}