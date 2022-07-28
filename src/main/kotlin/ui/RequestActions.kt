package ui

import bjda.plugins.ui.hook.MenuSelect.Companion.onSelect
import bjda.ui.component.Embed
import bjda.ui.component.Row
import bjda.ui.component.action.Menu
import bjda.ui.component.action.Menu.Companion.createOptions
import bjda.ui.core.Component
import bjda.ui.core.IProps
import bjda.ui.core.minus
import bjda.ui.core.rangeTo
import bjda.ui.types.Children
import net.dv8tion.jda.api.entities.Guild
import java.awt.Color

class RequestActions : Component<RequestActions.Props>(Props()) {
    class Props : IProps() {
        lateinit var guild: Guild
    }

    private val onChangeState by onSelect {

    }

    override fun onRender(): Children {
        return {
            + Embed()..{
                title = "Actions"
                color = Color.GREEN
            }

            + Row()-{
                + Menu(onChangeState) {
                    placeholder = "Change Request state"
                    options = createOptions(
                        selected = null,
                        "Example" to "value"
                    )
                }
            }
        }
    }
}