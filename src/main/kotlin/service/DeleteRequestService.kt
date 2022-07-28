package service

import database.deleteRequest
import net.dv8tion.jda.api.entities.Guild
import net.dv8tion.jda.api.entities.TextChannel

class DeleteRequestService(val guild: Guild, val id: Int) {
    fun delete(success: (TextChannel) -> Unit) {

        deleteRequest(guild.idLong, id) {
            val thread = guild.getTextChannelById(it.thread!!)

            thread?.delete()?.queue {
                success(thread)
            }
        }
    }
}