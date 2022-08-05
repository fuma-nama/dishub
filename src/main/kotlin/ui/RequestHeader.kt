package ui

import bjda.ui.component.Content
import bjda.ui.component.Embed
import bjda.ui.component.action.Button
import bjda.ui.component.row.Row
import bjda.ui.core.FComponent.Companion.component
import bjda.ui.core.IProps
import bjda.ui.core.rangeTo
import bjda.utils.blank
import bjda.utils.embed
import bjda.utils.field
import listeners.Methods
import listeners.RequestEvents
import models.tables.records.RequestInfoRecord
import models.tables.records.RequestRecord
import net.dv8tion.jda.api.entities.EmbedType
import variables.RequestState
import java.awt.Color
import java.time.format.DateTimeFormatter

class RequestHeaderProps : IProps() {
    lateinit var request: RequestRecord
    lateinit var info: RequestInfoRecord
}

val RequestHeader = component(::RequestHeaderProps) {

    with (props) {
        val onActions = Methods.request(RequestEvents.Actions, request.id!!)

        val tags = info.tags?.joinToString()
            ?: "No Tags"

        val state = RequestState.from(info.state!!);

        {
            + Content("**Request #${request.displayId}**")
            + Embed()..{
                title = info.title
                description = info.detail
                color = Color.GREEN
            }
            + Embed()..{
                fields = listOf(
                    field("State", "${state.emoji.formatted} ${state.name}", true),
                    field("Requested By", "<@${request.owner}>", true),
                    blank(),
                    field("Tags", tags, true),
                    field("Created at", request.createdAt!!.format(DateTimeFormatter.ISO_DATE), true)
                )
                color = Color.BLACK
            }

            + Row(
                Button.primary(id = onActions, label = "More")
            )
        }
    }
}