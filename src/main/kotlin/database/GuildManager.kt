package database

import ctx
import models.tables.Guild.Companion.GUILD
import models.tables.records.GuildRecord

fun getGuildSettings(id: Long): GuildRecord? {
    val settings = ctx.fetchOne(GUILD, GUILD.ID.eq(id))

    return settings
}

fun createGuildSettings(id: Long, user: Long, container: Long): GuildRecord? {
    return ctx.insertInto(GUILD, GUILD.ID, GUILD.USER_ROLE, GUILD.CONTAINER)
        .values(id, user, container)
        .onDuplicateKeyIgnore()
        .returning()
        .fetchOne()
}