package ui.panel

import bjda.ui.core.rangeTo
import bjda.utils.blank
import bjda.utils.field
import models.tables.records.RequestInfoRecord
import net.dv8tion.jda.api.entities.MessageEmbed
import net.dv8tion.jda.api.entities.User
import ui.SuccessPanel

val RequestUpdatePanel = { user: User, (old, new): Pair<RequestInfoRecord, RequestInfoRecord> ->
    SuccessPanel(user)..{
        title = "Modified the Request"

        fields = arrayListOf<MessageEmbed.Field>().apply {
            fun pair(name: String, old: String, new: String) {
                if (isNotEmpty()) {
                    add(
                        blank()
                    )
                }

                add(
                    field("Old $name", old, true)
                )
                add(
                    field("New $name", new, true)
                )
            }

            if (old.title != new.title) {
                pair("Title", old.title!!, new.title!!)
            }
            if (old.detail != new.detail) {
                pair("Detail", old.detail!!, new.detail!!)
            }
            if (!old.tags.contentEquals(new.tags)) {
                pair(
                    "Tags",
                    old.tags?.joinToString()?: "Empty",
                    new.tags?.joinToString()?: "Empty"
                )
            }
        }
    }
}