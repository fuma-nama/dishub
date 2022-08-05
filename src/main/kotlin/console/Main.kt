package console

import bjda.plugins.ui.UIEvent
import com.varabyte.kotter.foundation.session
import com.varabyte.kotter.foundation.text.textLine
import kotlinx.coroutines.delay

/**
 * Used for debug
 */
fun start() = session {

    section {
        with (UIEvent) {
            textLine("Buttons: ${buttons.size}")
            textLine("Modals: ${modals.size}")
            textLine("Select Menus: ${menus.size}")
        }

    }.run {
        while (true) {
            delay(1000)
            rerender()
        }
    }
}