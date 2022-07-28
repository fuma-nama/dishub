package service

import database.addGuildSettings
import database.getGuildSettings
import models.tables.records.GuildRecord
import net.dv8tion.jda.api.Permission
import net.dv8tion.jda.api.entities.Category
import net.dv8tion.jda.api.entities.Guild

class GuildSettingsService(val guild: Guild) {
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

    fun initSettings(success: (GuildRecord) -> Unit){
        val adminRole = guild.roles.find {
            it.hasPermission(Permission.ADMINISTRATOR)
        }

        fun create(container: Long) {
            val settings = addGuildSettings(
                id = guild.idLong,
                user = guild.publicRole.idLong,
                admin = adminRole?.idLong,
                manager = adminRole?.idLong,
                container = container
            ) ?: error("Unable to create guild settings")

            success(settings)
        }

        createContainer {
            create(it.idLong)
        }
    }
}