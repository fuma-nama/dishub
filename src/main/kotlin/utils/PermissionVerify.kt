package utils

import models.tables.records.GuildRecord
import models.tables.records.RequestRecord
import net.dv8tion.jda.api.Permission
import net.dv8tion.jda.api.entities.Guild
import net.dv8tion.jda.api.entities.Member
import net.dv8tion.jda.api.managers.channel.attribute.IPermissionContainerManager
import variables.NO_PERMISSIONS

val Guild.everyone: Long
    get() = this.publicRole.idLong

fun<M: IPermissionContainerManager<*, M>> M.allowUser(user: Long?, allow: Long, denyAll: Boolean = true): M {
    if (user == null) return this

    return putMemberPermissionOverride(user, allow,
        if (denyAll) Permission.ALL_PERMISSIONS else NO_PERMISSIONS
    )
}

fun<M: IPermissionContainerManager<*, M>> M.allowRole(role: Long?, allow: Long, denyAll: Boolean = true): M {
    if (role == null) return this

    return putRolePermissionOverride(role, allow,
        if (denyAll) Permission.ALL_PERMISSIONS else NO_PERMISSIONS
    )
}

fun RequestRecord.canEditRequest(user: Member): Boolean {
    return this.owner == user.idLong
}

fun GuildRecord.canModifyState(user: Member): Boolean {
    if (user.isOwner)
        return true

    return user.roles.any { role ->
        role.idLong == managerRole
    }
}

fun GuildRecord.canDeleteRequest(user: Member): Boolean {
    if (user.isOwner)
        return true

    return user.roles.any { role ->
        role.idLong == managerRole
    }
}