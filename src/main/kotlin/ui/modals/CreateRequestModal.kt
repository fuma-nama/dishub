package ui.modals

import bjda.ui.component.action.TextField.Companion.input
import bjda.ui.component.row.Row
import bjda.ui.core.hooks.Delegate
import bjda.ui.modal.modal
import listeners.Methods
import listeners.RequestEvents
import net.dv8tion.jda.api.interactions.components.text.TextInputStyle

val CreateRequestModal by Delegate {
    modal("Create a Request") {
        + Row(
            input(
                id = "title",
                label = "Title",
                placeholder = "Give your Request a Title",
                maxLength = 100)
        )

        + Row(
            input(
                id = "detail",
                label = "Detail",
                placeholder = "Describe your request clearly",
                style = TextInputStyle.PARAGRAPH
            )
        )

        + Row(
            input(
                id = "tags",
                label = "Tags (Split by space)",
                placeholder = "ex: feature bug improvement",
                style = TextInputStyle.SHORT,
                required = false
            )
        )
    }(Methods.request(RequestEvents.Create))
}