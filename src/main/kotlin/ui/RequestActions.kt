package ui

import bjda.ui.component.action.Button.Companion.secondary
import bjda.ui.component.action.Menu.Companion.menu
import bjda.ui.component.row.RowLayout
import bjda.ui.core.*
import bjda.ui.core.FElement.Companion.element
import bjda.utils.embed
import listeners.ActionEvents
import listeners.Methods
import models.tables.records.GuildRecord
import variables.RequestState as RequestState
import models.tables.records.RequestInfoRecord
import models.tables.records.RequestRecord
import net.dv8tion.jda.api.entities.Guild
import net.dv8tion.jda.api.entities.Member
import net.dv8tion.jda.api.interactions.components.selections.SelectOption
import utils.*
import java.awt.Color

class Props : IProps() {
    lateinit var request: RequestRecord
    lateinit var info: RequestInfoRecord
    lateinit var config: GuildRecord
    lateinit var owner: Member
    lateinit var guild: Guild
}

private class Option(state: RequestState, selected: Boolean) : SelectOption(
    state.name, state.name,
    state.description,
    selected,
    state.emoji
)

val RequestActions = element(::Props) {

    with (props) {
        val canModifyState = config.canModifyState(guild, owner)
        val canModifyTags = config.canModifyTags(guild, owner)
        val canEdit = request.canEditRequest(owner)

        val id = request.id!!
        val onEdit = Methods.action(ActionEvents.Open_Edit, id)
        val onModifyTags = Methods.action(ActionEvents.Open_Modify_Tags, id)
        val onChangeState = Methods.action(ActionEvents.Change_State, id);

        {
            + embed(
                title = "Actions",
                color = Color.GREEN
            )

            + RowLayout {
                if (canModifyState)
                    + menu(
                        id = onChangeState,
                        placeholder = "Change Request state",
                        options = RequestState.values().map {
                            Option(it, it.state == info.state)
                        }
                    )

                if (canEdit)
                    + secondary(id = onEdit, label = "Edit Request")
                if (canModifyTags)
                    + secondary(id = onModifyTags, label = "Modify Tags")
            }
        }
    }
}