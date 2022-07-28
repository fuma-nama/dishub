package database

import ctx
import models.tables.Guild.Companion.GUILD
import models.tables.records.GuildRecord

fun getGuildSettings(id: Long): GuildRecord? {
    val settings = ctx.fetchOne(GUILD, GUILD.ID.eq(id))

    return settings
}

fun addGuildSettings(id: Long, user: Long, admin: Long?, manager: Long?, container: Long): GuildRecord? {
    return ctx.insertInto(GUILD, GUILD.ID, GUILD.ADMIN_ROLE, GUILD.MANAGER_ROLE, GUILD.USER_ROLE, GUILD.CONTAINER)
        .values(id, admin, manager, user, container)
        .onDuplicateKeyIgnore()
        .returning()
        .fetchOne()
}