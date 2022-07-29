package service.request

import bjda.ui.core.UIOnce
import database.addRequest
import database.addSubscriber
import database.createInfo
import models.tables.records.GuildRecord
import models.tables.records.RequestInfoRecord
import models.tables.records.RequestRecord
import net.dv8tion.jda.api.Permission.*
import net.dv8tion.jda.api.entities.Guild
import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.entities.TextChannel
import net.dv8tion.jda.api.entities.User
import net.dv8tion.jda.api.requests.RestAction
import service.GuildSettingsService
import ui.RequestHeader
import utils.combine
import variables.ADMIN_PERMISSIONS
import variables.NO_PERMISSIONS
import variables.USER_PERMISSIONS
import variables.VIEW_PERMISSION

class CreateRequestService(val guild: Guild) {
    private val settings = GuildSettingsService(guild)
    lateinit var thread: TextChannel
    lateinit var config: GuildRecord

    /**
     * Create a new Request
     */
    fun create(option: RequestOption, success: (RequestRecord) -> Unit) {
        val requester = option.requester.idLong

        fun init(info: RequestInfoRecord, request: RequestRecord) {

            val requestId = request.id!!

            combine(
                initThread(requestId),
                initHeader(info, request)
            ) {
                addSubscriber(requester, guild.idLong, requestId)

                success(request)
            }
        }

        settings.getOrInit {
            config = it

            createThread { thread ->
                createHeader { header ->
                    val request = addRequest(guild.idLong, requester, thread.idLong, header.idLong)
                        ?: error("Failed to create request")

                    val info = createInfo(request, option.title, option.description)!!

                    init(info, request)
                }
            }
        }
    }

    fun createThread(success: (TextChannel) -> Unit) {
        val container = guild.getCategoryById(config.container!!)
        val action = guild.createTextChannel("empty", container)
            .addPermissionOverride(
                guild.publicRole,
                NO_PERMISSIONS,
                ALL_PERMISSIONS
            )

        action.queue {channel ->
            this.thread = channel

            success(channel)
        }
    }

    private fun createHeader(onSuccess: (Message) -> Unit) {

        thread.sendMessage("Creating Request...").queue(onSuccess)
    }

    private fun initThread(request: Int): RestAction<*> {
        val action = thread.manager.setName("request-$request")

        fun allow(role: Long?, allow: Long) {
            if (role == null) return

            action.putRolePermissionOverride(role, allow, 0L)
        }

        with (config) {

            allow(userRole, USER_PERMISSIONS)
            allow(managerRole, USER_PERMISSIONS)
            allow(adminRole, ADMIN_PERMISSIONS)
        }

        return action
    }

    private fun initHeader(info: RequestInfoRecord, request: RequestRecord): RestAction<*> {
        val ui = UIOnce(RequestHeader {
            this.request = request
            this.info = info
        })

        return thread.editMessageById(request.headerMessage!!, ui.get())
    }

    fun addSubscriber(requester: Long, guild: Long, request: Int) {
        addSubscriber(requester, guild, request) {
            onSubscribed(requester).queue()
        }
    }

    private fun onSubscribed(user: Long): RestAction<*> {
        return thread.manager.putMemberPermissionOverride(user, VIEW_PERMISSION, 0L)
    }
}

data class RequestOption(val title: String, val description: String, val requester: User)