package ui

import bjda.plugins.ui.modal.Form
import bjda.ui.component.Embed
import bjda.ui.component.Row
import bjda.ui.component.action.Button
import bjda.ui.component.action.TextField
import bjda.ui.core.*
import bjda.ui.types.ComponentTree
import net.dv8tion.jda.api.interactions.components.buttons.ButtonStyle
import net.dv8tion.jda.api.interactions.components.text.TextInputStyle
import service.CreateRequestService
import service.RequestOption
import java.awt.Color

val CreateRequestModal = Form {
    title = "Create a Request"

    onSubmit = { event ->
        val guild = event.guild!!

        event.deferReply(true).queue { hook ->

            val info = RequestOption(
                event.value("title"),
                event.value("detail"),
                event.user
            )

            CreateRequestService(guild).create(info) { request ->

                val panel = SuccessPanel()..{
                    requestId = request.id!!
                    threadUrl = "https://discord.com/channels/${guild.id}/${request.thread}"
                }

                hook.editOriginal(
                    UIOnce(panel).get()
                ).queue()
            }
        }
    }

    render = {
        +row(
            TextField("title") {
                label = "Title"
                placeholder = "Give your Request a Title"
                maxLength = 100
            }
        )
        +row(
            TextField("detail") {
                label = "Detail"
                placeholder = "Describe your request clearly"
                style = TextInputStyle.PARAGRAPH
            }
        )
    }
}

private class SuccessPanel : ElementImpl<SuccessPanel.Props>(Props()) {
    class Props: IProps() {
        var requestId: Int = 0
        lateinit var threadUrl: String
    }

    override fun render(): ComponentTree {
        return arrayOf(
            Embed()..{
                title = "Request Created: #${props.requestId}"
                description = "You may open request thread by the button below"
                color = Color.GREEN
            },
            Row() -{
                + Button {
                    label = "Open Thread"
                    url = props.threadUrl
                    style = ButtonStyle.LINK
                }
            }
        )
    }
}