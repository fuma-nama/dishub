package service.request

import bjda.ui.core.UIOnce.Companion.buildMessage
import database.addRequest
import database.addSubscriber
import database.createInfo
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import models.tables.records.GuildRecord
import models.tables.records.RequestInfoRecord
import models.tables.records.RequestRecord
import net.dv8tion.jda.api.Permission.*
import net.dv8tion.jda.api.entities.Guild
import net.dv8tion.jda.api.entities.TextChannel
import net.dv8tion.jda.api.entities.User
import net.dv8tion.jda.api.requests.RestAction
import service.GuildSettingsService
import service.Service
import ui.RequestHeader
import utils.*
import variables.*

class CreateRequestService(val guild: Guild): Service {
    private val settings = GuildSettingsService(guild)
    lateinit var thread: TextChannel

    /**
     * Create a new Request
     */
    fun create(option: RequestOption, success: (RequestRecord) -> Unit) = launch {
        val requester = option.requester.idLong
        val config = settings.getOrInit()
        val thread = createThread(config.container!!)
        val header = createHeader()

        val request = addRequest(guild.idLong, requester, thread.idLong, header.idLong)
            ?: error("Failed to create request")

        val info = createInfo(request, option.title, option.description, option.tags)!!

        combine(
            initThread(request.id!!, requester, config),
            initHeader(info, request)
        ).queue {
            success(request)
        }
    }

    suspend fun createThread(containerId: Long) = coroutineScope {
        val container = guild.getCategoryById(containerId)
        val action = guild.createTextChannel("empty", container)
            .addPermissionOverride(guild.publicRole, NO_PERMISSIONS, ALL_PERMISSIONS)

        action.queueAsync().also {
            thread = it
        }
    }

    private suspend fun createHeader() = coroutineScope {

        thread.sendMessage("Creating Request...").queueAsync()
    }

    private fun initThread(request: Int, owner: Long, config: GuildRecord): RestAction<*> {
        addSubscriber(owner, guild.idLong, request)

        return thread.manager.apply {
            setName("request-$request")

            with (config) {
                allowUser(owner, VIEW_PERMISSION, denyAll = false)
                allowRole(managerRole, MANAGER_PERMISSIONS)
                allowRole(guild.everyone, OPENING_PERMISSIONS)
            }
        }
    }

    private fun initHeader(info: RequestInfoRecord, request: RequestRecord): RestAction<*> {
        val ui = RequestHeader {
            this.request = request
            this.info = info
        }

        return thread.editMessageById(request.headerMessage!!, ui.buildMessage())
    }
}

class RequestOption(val title: String, val description: String, val requester: User, val tags: Array<String>?)