package ui.modals

import bjda.plugins.ui.modal.modal
import bjda.ui.component.action.TextField.Companion.input
import bjda.ui.component.row.Row
import listeners.ActionEvents
import listeners.Methods
import models.tables.records.RequestInfoRecord
import net.dv8tion.jda.api.interactions.components.Modal
import net.dv8tion.jda.api.interactions.components.text.TextInputStyle

fun EditRequestModal(request: Int, info: RequestInfoRecord): Modal {
    val id = Methods.action(ActionEvents.Edit, request)

    return modal("Modify Your Request",
        Row(
            input(
                id = "title",
                label = "Title",
                placeholder = "Give your Request a Title",
                value = info.title,
                maxLength = 100
            )
        ),
        Row(
            input(
                id = "detail",
                label = "Detail",
                placeholder = "Describe your request clearly",
                value = info.detail,
                style = TextInputStyle.PARAGRAPH,
            )
        )
    )(id)
}