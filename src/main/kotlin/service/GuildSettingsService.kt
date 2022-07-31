package service

import database.createGuildSettings
import database.getGuildSettings
import database.updateGuildContainer
import database.updateGuildManager
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import models.tables.records.GuildRecord
import net.dv8tion.jda.api.entities.Guild
import utils.queueAsync

class GuildSettingsService(val guild: Guild): Service {
    suspend fun getOrInit() = coroutineScope {
        val settings = getGuildSettings(guild.idLong)

        settings?: initSettings()
    }

    fun getOrInit(success: (GuildRecord) -> Unit) = launch {
        success(getOrInit())
    }

    private suspend fun createContainer() = coroutineScope {
        guild.createCategory("Threads").queueAsync()
    }

    private suspend fun initSettings() = coroutineScope {

        val container = createContainer()

        createGuildSettings(
            id = guild.idLong,
            container = container.idLong
        ) ?: error("Unable to create guild settings")
    }

    fun updateContainer(container: Long) {
        updateGuildContainer(guild.idLong, container)
    }

    fun updateManagerRole(role: Long) {
        updateGuildManager(guild.idLong, role)
    }
}