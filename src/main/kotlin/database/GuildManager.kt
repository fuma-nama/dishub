package database

import ctx
import models.tables.Guild.Companion.GUILD
import models.tables.records.GuildRecord

fun getGuildSettings(id: Long): GuildRecord? {

    return ctx.fetchOne(GUILD, GUILD.ID.eq(id))
}

fun createGuildSettings(id: Long, container: Long): GuildRecord? {
    return ctx.insertInto(GUILD, GUILD.ID, GUILD.CONTAINER)
        .values(id, container)
        .onDuplicateKeyIgnore()
        .returning()
        .fetchOne()
}

fun updateGuildSettings(id: Long, container: Long) {
    with (GUILD) {
        ctx.update(this)
            .set(CONTAINER, container)
            .where(ID.eq(id))
            .executeAsync()
    }
}