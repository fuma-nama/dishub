package service.request

import bjda.ui.core.UIOnce.Companion.buildMessage
import database.editRequest
import database.modifyRequestTags
import database.setRequestState
import models.enums.State
import models.tables.records.RequestInfoRecord
import models.tables.records.RequestRecord
import net.dv8tion.jda.api.entities.Guild
import net.dv8tion.jda.api.requests.RestAction
import ui.RequestHeader
import utils.allowRole
import variables.NO_PERMISSIONS
import variables.OPENING_PERMISSIONS

class UpdateRequestService(val guild: Guild, var request: RequestRecord, var info: RequestInfoRecord) {
    val thread by lazy {
        guild.getTextChannelById(request.thread!!)?: error("Failed to find thread channel")
    }

    fun updateHeader(): RestAction<*> {
        val service = this

        val ui = RequestHeader {
            this.request = service.request
            this.info = service.info
        }

        return thread.editMessageById(request.headerMessage!!, ui.buildMessage())
    }

    fun updatePermissions(): RestAction<*> {
        val everyone = guild.publicRole.idLong

        return when (info.state) {
            State.opening, State.processing -> {
                thread.manager.allowRole(
                    everyone, OPENING_PERMISSIONS
                )
            }
            else -> {
                thread.manager.allowRole(
                    everyone, NO_PERMISSIONS
                )
            }
        }
    }

    suspend fun updateTags(tags: Array<String>?): Pair<RequestInfoRecord, RequestInfoRecord>? {
        val updated = modifyRequestTags(request.id!!, tags)
            ?: return null

        val old = info
        info = updated

        return old to updated
    }

    suspend fun updateRequest(title: String, description: String): Pair<RequestInfoRecord, RequestInfoRecord>? {
        val updated = editRequest(
            info.request!!,
            title, description
        )?: return null

        val old = info
        info = updated

        return old to updated
    }

    suspend fun updateState(state: State): Boolean {
        val updated = setRequestState(info.request!!, state)?.also {
            info = it
        }

        return updated != null
    }
}