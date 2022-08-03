package ui.modals

import bjda.plugins.ui.modal.ModalPool
import bjda.plugins.ui.modal.modal
import bjda.ui.component.Embed
import bjda.ui.component.action.Button
import bjda.ui.component.action.TextField.Companion.input
import bjda.ui.component.row.Row
import bjda.ui.core.*
import bjda.ui.core.UIOnce.Companion.buildMessage
import bjda.ui.core.hooks.Delegate
import bjda.ui.types.ComponentTree
import net.dv8tion.jda.api.interactions.components.text.TextInputStyle
import service.request.CreateRequestService
import service.request.RequestOption
import utils.parseTags
import java.awt.Color

val CreateRequestModal by Delegate {
    pool.next()
}

private val pool = ModalPool.fixed(
    creator = modal("Create a Request") {
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
    }
) { event ->
    val guild = event.guild!!

    event.deferReply(true).queue { hook ->

        val info = RequestOption(
            event.value("title"),
            event.value("detail"),
            event.user,
            parseTags(event["tags"])
        )

        CreateRequestService(guild).create(info) { request ->

            val panel = SuccessPanel()..{
                requestId = request.id!!
                threadUrl = "https://discord.com/channels/${guild.id}/${request.thread}"
            }

            hook.editOriginal(
                panel.buildMessage()
            ).queue()
        }
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
            Row(
                Button.link(
                    label = "Open Thread",
                    url = props.threadUrl
                )
            )
        )
    }
}