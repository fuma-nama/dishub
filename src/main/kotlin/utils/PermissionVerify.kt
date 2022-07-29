package utils

import models.tables.records.GuildRecord
import models.tables.records.RequestRecord
import net.dv8tion.jda.api.entities.Member
import service.GuildSettingsService
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

fun RequestRecord.canEditRequest(user: Member): Boolean {
    return this.owner == user.idLong
}

fun GuildRecord.canModifyState(user: Member): Boolean {
    if (user.isOwner)
        return true

    return user.roles.any { role ->
        role.idLong == managerRole || role.idLong == adminRole
    }
}

fun GuildRecord.canDeleteRequest(user: Member): Boolean {
    if (user.isOwner)
        return true

    return user.roles.any { role ->
        role.idLong == adminRole
    }
}