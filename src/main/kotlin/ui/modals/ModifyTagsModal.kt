package ui.modals

import bjda.ui.modal.modal
import bjda.ui.component.action.TextField.Companion.input
import bjda.ui.component.row.Row
import listeners.ActionEvents
import listeners.Methods
import models.tables.records.RequestInfoRecord

val ModifyTagsModal = {request: Int, info: RequestInfoRecord ->
    val id = Methods.action(ActionEvents.Modify_Tags, request)

    modal("Modify Tags") {
        + Row(
            input(
                id = "tags",
                label = "Tags (Split by space)",
                placeholder = "ex: feature bug improvement",
                value = info.tags?.joinToString(separator = " "),
                required = false
            )
        )
    }(id)
}