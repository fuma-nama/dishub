package database

import ctx
import kotlinx.coroutines.coroutineScope
import models.enums.State
import models.tables.records.RequestInfoRecord
import models.tables.records.RequestRecord
import models.tables.records.SubscriptionRecord
import models.tables.references.REQUEST
import models.tables.references.REQUEST_INFO
import models.tables.references.SUBSCRIPTION
import org.jooq.*
import org.jooq.impl.DSL.*
import ui.panel.MAX_REQUESTS

/**
 * Insert a new request
 *SELECT coalesce(max("itemNumber"),0) + 1
FROM "OrderItem"
WHERE "orderId"=3)
 * @return Created request id
 */
fun addRequest(guild: Long, owner: Long, thread: Long, message: Long): RequestRecord? {
    with (REQUEST) {

        val nextDisplayId = select(coalesce(max(DISPLAY_ID)).plus(1))
            .from(this)
            .where(GUILD.eq(guild))

        return ctx.insertInto(this, GUILD, OWNER, THREAD, HEADER_MESSAGE, DISPLAY_ID)
            .values( `val`(guild), `val`(owner), `val`(thread), `val`(message), nextDisplayId.asField())
            .returning()
            .fetchOne()
    }
}

suspend fun listRequests(
    guild: Long,
    offset: Int,
    limit: Int = MAX_REQUESTS,
    filter: Filter
) = coroutineScope {

    with (REQUEST) {

        ctx.select(this, REQUEST_INFO).from(this)
            .let {
                filter.joinInfo(it)

                if (filter.fetchSubscription) {
                    filter.joinSubscription(it)
                }

                filter.where(it, GUILD.eq(guild))
            }
            .orderBy(CREATED_AT.desc().nullsLast())
            .offset(offset)
            .limit(limit)
            .fetch()
    }
}

fun countRequest(guild: Long, filter: Filter): Int? {
    with (REQUEST) {

        val (count) = ctx.selectCount().from(this)
            .apply {
                filter.filter(this, GUILD.eq(guild))
            }
            .fetchOne()
            ?: return null

        return count
    }
}

fun getRequest(id: Int): RequestRecord? {
    with (REQUEST) {
        return ctx.fetchOne(this, ID.eq(id))
    }
}

fun getRequest(guild: Long, displayId: Int): RequestRecord? {
    with (REQUEST) {
        return ctx.fetchOne(this, GUILD.eq(guild), DISPLAY_ID.eq(displayId))
    }
}


fun getRequestByThread(guild: Long, thread: Long): RequestRecord? {
    with (REQUEST) {
        return ctx.fetchOne(this, GUILD.eq(guild), THREAD.eq(thread))
    }
}

fun deleteRequest(guild: Long, request: Int, success: (RequestRecord) -> Unit) {

    with (REQUEST) {

        ctx.delete(this)
            .where(GUILD.eq(guild), ID.eq(request))
            .returning()
            .fetchOneAsync(success)
    }
}

fun addSubscriber(user: Long, request: Int) {
    with (SUBSCRIPTION) {

        ctx.insertInto(this, USER, REQUEST)
            .values(user, request)
            .onDuplicateKeyIgnore()
            .execute()
    }
}

fun removeSubscriber(user: Long, request: Int): SubscriptionRecord? {
    with (SUBSCRIPTION) {

        return ctx.delete(this)
            .where(USER.eq(user), REQUEST.eq(request))
            .returning()
            .fetchOne()
    }
}

fun createInfo(request: RequestRecord, title: String, detail: String, tags: Array<String>?): RequestInfoRecord? {
    return createInfo(request.id!!, title, detail, tags)
}

fun createInfo(request: Int, title: String, detail: String, tags: Array<String>?): RequestInfoRecord? {
    with (REQUEST_INFO) {
        return ctx.insertInto(this, REQUEST, TITLE, DETAIL, TAGS)
            .values(request, title, detail, tags as Array<String?>?)
            .returning()
            .fetchOne()
    }
}

suspend fun fetchRequestFull(guild: Long, request: Int) = coroutineScope {

    with (REQUEST) {
        ctx.select(this, REQUEST_INFO).from(this)
            .join(REQUEST_INFO)
            .on(REQUEST_INFO.REQUEST.eq(ID))
            .where(ID.eq(request), GUILD.eq(guild))
            .fetchOne()
    }
}

suspend fun modifyRequestTags(id: Int, tags: Array<String>?) = coroutineScope {

    with (REQUEST_INFO) {
        ctx.update(this)
            .set(TAGS,
                if (tags == null || tags.isEmpty()) {
                    null
                } else {
                    tags as Array<String?>
                }
            )
            .where(REQUEST.eq(id))
            .returning()
            .fetchOne()
    }
}

suspend fun editRequest(id: Int, title: String, detail: String) = coroutineScope {
    with (REQUEST_INFO) {
        ctx.update(this)
            .set(TITLE, title)
            .set(DETAIL, detail)
            .where(REQUEST.eq(id))
            .returning()
            .fetchOne()
    }
}

suspend fun setRequestState(request: Int, state: State) = coroutineScope {

    with (REQUEST_INFO) {
        ctx.update(this)
            .set(STATE, state)
            .where(REQUEST.eq(request))
            .returning()
            .fetchOne()
    }
}

fun fetchRequestInfo(request: Int): RequestInfoRecord? {
    with (REQUEST_INFO) {
        return ctx.fetchOne(this, REQUEST.eq(request))
    }
}

data class Filter(val conditions: List<Condition>, val fetchInfo: Boolean, val fetchSubscription: Boolean) {

    fun<T : Record?> where(
        step: SelectJoinStep<T>,
        vararg conditions: Condition
    ): SelectConditionStep<T> {

        return step.where(this@Filter.conditions + conditions)
    }

    fun<T : Record?> filter(
        step: SelectJoinStep<T>,
        vararg conditions: Condition
    ): SelectConditionStep<T> {

        if (fetchInfo) {
            joinInfo(step)
        }

        if (fetchSubscription) {
            joinSubscription(step)
        }

        return step.where(this@Filter.conditions + conditions)
    }

    fun<T : Record?> joinInfo(step: SelectJoinStep<T>) = with (REQUEST) {
        step.join(REQUEST_INFO).on(REQUEST_INFO.REQUEST.eq(ID))
    }

    fun<T : Record?> joinSubscription(step: SelectJoinStep<T>) = with (REQUEST) {
        step.join(SUBSCRIPTION).on(SUBSCRIPTION.REQUEST.eq(ID))
    }

    companion object {

        fun build(keyword: String?, author: Long?, state: State?, subscribedBy: Long?): Filter {
            with (REQUEST) {

                val conditions = ArrayList<Condition>().apply {

                    if (author != null)
                        add(OWNER.eq(author))

                    if (keyword != null)
                        add(REQUEST_INFO.TITLE.like(keyword))

                    if (state != null)
                        add(REQUEST_INFO.STATE.eq(state))

                    if (subscribedBy != null)
                        add(SUBSCRIPTION.USER.eq(subscribedBy))
                }

                return Filter(conditions, state != null || keyword != null, subscribedBy != null)
            }
        }
    }
}
