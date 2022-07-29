package service

import database.createGuildSettings
import database.getGuildSettings
import models.tables.records.GuildRecord
import net.dv8tion.jda.api.entities.Category
import net.dv8tion.jda.api.entities.Guild
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class GuildSettingsService(val guild: Guild) {
    suspend fun getOrInit() = suspendCoroutine { cont ->
        getOrInit(cont::resume)
    }

    fun getOrInit(success: (GuildRecord) -> Unit) {
        val settings = getGuildSettings(guild.idLong)

        if (settings != null) {
            success(settings)
        } else {
            initSettings(success)
        }
    }

    fun createContainer(success: (Category) -> Unit) {
        guild.createCategory("Threads").queue(success)
    }

    fun initSettings(success: (GuildRecord) -> Unit) {

        createContainer { container ->

            val settings = createGuildSettings(
                id = guild.idLong,
                user = guild.publicRole.idLong,
                container = container.idLong
            ) ?: error("Unable to create guild settings")

            success(settings)
        }
    }
}