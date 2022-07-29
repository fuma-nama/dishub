package ui.modals

import bjda.plugins.ui.modal.Form
import bjda.ui.component.Embed
import bjda.ui.component.Row
import bjda.ui.component.action.Button
import bjda.ui.component.action.TextField
import bjda.ui.core.*
import bjda.ui.types.ComponentTree
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent
import net.dv8tion.jda.api.interactions.components.buttons.ButtonStyle
import net.dv8tion.jda.api.interactions.components.text.TextInputStyle
import ui.RequestActions
import java.awt.Color

fun RequestActions.EditRequestModal(onEdit: (event: ModalInteractionEvent) -> Unit): Form {
    return Form {
        title = "Modify Your Request"

        onSubmit = onEdit

        render = {
            val info = props.info

            +row(
                TextField("title") {
                    label = "Title"
                    placeholder = "Give your Request a Title"
                    value = info.title
                    maxLength = 100
                }
            )
            +row(
                TextField("detail") {
                    label = "Detail"
                    placeholder = "Describe your request clearly"
                    value = info.detail
                    style = TextInputStyle.PARAGRAPH
                }
            )
        }
    }
}