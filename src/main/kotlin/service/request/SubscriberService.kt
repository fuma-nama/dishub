package service.request

import models.tables.records.RequestRecord
import net.dv8tion.jda.api.entities.Guild
import net.dv8tion.jda.api.entities.Member
import utils.allowUser
import utils.queueAsync
import variables.VIEW_PERMISSION

class SubscriberService(val guild: Guild, val request: RequestRecord) {
    val thread by lazy {
        guild.getTextChannelById(request.thread!!)?: error("Failed to find thread channel")
    }

    suspend fun addSubscriber(user: Member): SubscriberService {
        database.addSubscriber(user.idLong, guild.idLong, request.id!!)

        thread.manager.apply {
            allowUser(user.idLong, VIEW_PERMISSION, false)

            queueAsync()
        }

        return this
    }
}