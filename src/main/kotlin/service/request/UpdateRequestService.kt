package service.request

import bjda.ui.core.UIOnce
import database.editRequest
import database.setRequestState
import models.enums.State
import models.tables.records.RequestInfoRecord
import models.tables.records.RequestRecord
import net.dv8tion.jda.api.entities.Guild
import net.dv8tion.jda.api.requests.RestAction
import ui.RequestHeader

class UpdateRequestService(val guild: Guild, var request: RequestRecord, var info: RequestInfoRecord) {
    fun updateHeader(): RestAction<*> {
        val service = this
        val thread = guild.getTextChannelById(request.thread!!)?: error("Failed to find thread channel")

        val ui = UIOnce(RequestHeader {
            this.request = service.request
            this.info = service.info
        })

        return thread.editMessageById(request.headerMessage!!, ui.get())
    }

    suspend fun updateRequest(title: String, description: String): Boolean {
        val updated = editRequest(
            info.guild!!, info.request!!,
            title, description
        )

        if (updated != null) {
            info = updated
        }

        return updated != null
    }

    suspend fun updateState(state: State): Boolean {
        val updated = setRequestState(info.guild!!, info.request!!, state)?.also {
            info = it
        }

        return updated != null
    }
}