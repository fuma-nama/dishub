package ui.panel

import bjda.ui.component.utils.Builder
import bjda.utils.author
import bjda.utils.embed
import listeners.Methods
import listeners.RequestEvents
import models.tables.records.RequestInfoRecord
import models.tables.records.RequestRecord
import net.dv8tion.jda.api.entities.Member
import net.dv8tion.jda.api.interactions.components.ActionRow
import net.dv8tion.jda.api.interactions.components.buttons.Button
import utils.getJumpUrl

val ReferencePanel = { author: Member, request: RequestRecord, info: RequestInfoRecord ->
    val name = author.nickname?: author.user.name
    val avatar = author.avatarUrl?: author.user.avatarUrl
    val subscribe = Methods.request(RequestEvents.Subscribe, request.id!!)

    Builder {
        it.addEmbeds(
            embed(
                title = "$name Mentioned Request: #${info.request}",
                description = info.title,
                author = author(
                    name = name,
                    icon = avatar
                ),
            )
        )

        it.addActionRow(
            ActionRow.of(
                Button.secondary(subscribe, "Subscribe"),
                Button.link(getJumpUrl(request), "Open")
            )
        )
    }
}