package ui.panel

import bjda.utils.embed
import java.awt.Color

val UnsubscribedPanel = { request: Int ->
    embed(
        title = "Unsubscribed to the Request: #$request",
        description = "You will be unable to view and open the thread until you subscribed to it again",
        color = Color.GREEN
    )
}