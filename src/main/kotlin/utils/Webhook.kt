package utils

import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.entities.IWebhookContainer
import net.dv8tion.jda.api.entities.Icon
import net.dv8tion.jda.api.entities.Webhook
import net.dv8tion.jda.api.entities.channel.unions.IWebhookContainerUnion
import net.dv8tion.jda.api.requests.Request
import net.dv8tion.jda.api.requests.Response
import net.dv8tion.jda.api.requests.restaction.WebhookAction
import net.dv8tion.jda.api.utils.data.DataObject
import net.dv8tion.jda.internal.requests.Route
import net.dv8tion.jda.internal.requests.restaction.AuditableRestActionImpl
import net.dv8tion.jda.internal.utils.Checks
import okhttp3.RequestBody
import java.util.concurrent.TimeUnit
import java.util.function.BooleanSupplier
import javax.annotation.CheckReturnValue

class WebhookAction(api: JDA?, private val channel: IWebhookContainer, var name: String) :
    AuditableRestActionImpl<Webhook>(
        api, Route.Channels.CREATE_WEBHOOK.compile(
            channel.id
        )
    ),
    WebhookAction {
    var avatar: String? = null

    override fun setCheck(checks: BooleanSupplier?): utils.WebhookAction {
        return super.setCheck(checks) as utils.WebhookAction
    }

    override fun timeout(timeout: Long, unit: TimeUnit): utils.WebhookAction {
        return super.timeout(timeout, unit) as utils.WebhookAction
    }

    override fun deadline(timestamp: Long): utils.WebhookAction {
        return super.deadline(timestamp) as utils.WebhookAction
    }

    override fun getChannel(): IWebhookContainerUnion {
        return channel as IWebhookContainerUnion
    }

    @CheckReturnValue
    override fun setName(name: String): utils.WebhookAction {
        Checks.notEmpty(name, "Name")
        Checks.notLonger(name, 100, "Name")
        this.name = name
        return this
    }

    @CheckReturnValue
    fun setAvatarUrl(url: String?): utils.WebhookAction {
        avatar = url
        return this
    }

    @CheckReturnValue
    override fun setAvatar(icon: Icon?): utils.WebhookAction {
        avatar = icon?.encoding
        return this
    }

    public override fun finalizeData(): RequestBody {
        val data = DataObject.empty().apply {
            put("name", name)
            put("avatar", avatar)
        }

        return this.getRequestBody(data)
    }

    override fun handleSuccess(response: Response, request: Request<Webhook>) {
        val json = response.getObject()
        val webhook: Webhook = api.entityBuilder.createWebhook(json)
        request.onSuccess(webhook)
    }
}