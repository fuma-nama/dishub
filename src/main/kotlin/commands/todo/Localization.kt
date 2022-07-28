package commands.todo

import bjda.utils.Translation
import bjda.utils.Translation.Companion.group
import net.dv8tion.jda.api.interactions.DiscordLocale

val en = group(
    "todo" to "TODO",
    "title" to "Todo Panel",
    "add" to "Add",
    "edit" to "Modify",
    "delete" to "Delete",
    "placeholder" to "No Todo yet...",
    "close" to "Close Panel"
)(
    "menu" to group(
        "placeholder" to "Select a Todo"
    ),
    "form" to group(
        "new_content" to "New Content"
    ),
)

val ch = group(
    "todo" to "待辦事項",
    "title" to "待辦事項面板",
    "add" to "添加",
    "edit" to "編輯",
    "delete" to "刪除",
    "placeholder" to "還沒有待辦事項",
    "close" to "關閉面板"
)(
    "menu" to group(
        "placeholder" to "選擇一個待辦事項"
    ),
    "form" to group(
        "new_content" to "新內容"
    ),
)

fun getTranslation(locale: DiscordLocale): Translation {
    return when (locale) {
        DiscordLocale.CHINESE_CHINA, DiscordLocale.CHINESE_TAIWAN -> ch
        else -> en
    }
}