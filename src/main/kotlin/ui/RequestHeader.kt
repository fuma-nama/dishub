package ui

import bjda.ui.component.Content
import bjda.ui.component.Embed
import bjda.ui.component.Row
import bjda.ui.component.action.Button
import bjda.ui.core.FComponent.Companion.component
import bjda.ui.core.IProps
import bjda.ui.core.minus
import bjda.ui.core.rangeTo
import listeners.Methods
import models.tables.records.RequestInfoRecord
import models.tables.records.RequestRecord
import java.awt.Color

class RequestHeaderProps : IProps() {
    lateinit var request: RequestRecord
    lateinit var info: RequestInfoRecord
}

val RequestHeader = component(::RequestHeaderProps) {

    with (props) {
        val onModify = Methods.build(Methods.Modify, request.id!!)

        val tags = arrayOf("Feature").joinToString();

        {
            + Content("**Request #${request.id}**")
            + Embed()..{
                title = info.title
                description = info.detail
                color = Color.GREEN
            }
            + Embed()..{
                fields = fields(
                    field("State" to info.state!!.name, true),
                    field("Requester" to "<@${request.owner}>", true),
                    field("Tags" to tags)
                )
                color = Color.BLACK
            }

            + Row()-{
                + Button(onModify) {
                    label = "Modify"
                }
            }
        }
    }
}