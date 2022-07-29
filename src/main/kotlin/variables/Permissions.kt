package variables

import net.dv8tion.jda.api.Permission

const val NO_PERMISSIONS = 0L
val USER_PERMISSIONS = Permission.getRaw(
    Permission.MESSAGE_ADD_REACTION,
    Permission.MESSAGE_SEND,
    Permission.MESSAGE_EMBED_LINKS,
    Permission.MESSAGE_ATTACH_FILES,
    Permission.MESSAGE_EXT_EMOJI,
    Permission.MESSAGE_EXT_STICKER,
    Permission.MESSAGE_HISTORY,
    Permission.USE_APPLICATION_COMMANDS,
)

val ADMIN_PERMISSIONS = Permission.ALL_TEXT_PERMISSIONS

val VIEW_PERMISSION = Permission.getRaw(
    Permission.VIEW_CHANNEL,
    Permission.MESSAGE_HISTORY,
)