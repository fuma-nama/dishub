package variables

import net.dv8tion.jda.api.Permission

const val NO_PERMISSIONS = 0L
const val MANAGER_PERMISSIONS = NO_PERMISSIONS

val OPENING_PERMISSIONS = Permission.getRaw(
    Permission.MESSAGE_SEND,
    Permission.MESSAGE_ADD_REACTION,
    Permission.MESSAGE_EMBED_LINKS,
    Permission.MESSAGE_ATTACH_FILES,
    Permission.MESSAGE_EXT_EMOJI,
    Permission.MESSAGE_EXT_STICKER,
    Permission.USE_APPLICATION_COMMANDS,
    Permission.MESSAGE_HISTORY,
)

val VIEW_PERMISSION = Permission.getRaw(
    Permission.VIEW_CHANNEL,
    Permission.MESSAGE_HISTORY,
)