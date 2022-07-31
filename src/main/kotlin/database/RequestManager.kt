package database

import ctx
import kotlinx.coroutines.coroutineScope
import models.enums.State
import models.tables.records.RequestInfoRecord
import models.tables.records.RequestRecord
import models.tables.references.REQUEST
import models.tables.references.REQUEST_INFO
import models.tables.references.SUBSCRIPTION
import org.jooq.Record2
import java.util.function.Consumer

/**
 * Insert a new request
 *
 * @return Created request id
 */
fun addRequest(guild: Long, owner: Long, thread: Long, message: Long): RequestRecord? {
    with (REQUEST) {

        return ctx.insertInto(this, GUILD, OWNER, THREAD, HEADER_MESSAGE)
            .values(guild, owner, thread, message)
            .returning()
            .fetchOne()
    }
}

suspend fun listRequests(guild: Long, offset: Int) = coroutineScope {
    with (REQUEST) {
        ctx.select(this, REQUEST_INFO).from(this)
            .join(REQUEST_INFO).on(REQUEST_INFO.GUILD.eq(GUILD), REQUEST_INFO.REQUEST.eq(ID))
            .where(GUILD.eq(guild))
            .offset(offset)
            .limit(10)
            .fetch()
    }
}

fun countRequest(guild: Long): Int {
    with (REQUEST) {
        return ctx.fetchCount(this, GUILD.eq(guild))
    }
}

fun getRequest(guild: Long, id: Int): RequestRecord? {
    with (REQUEST) {
        return ctx.fetchOne(this, GUILD.eq(guild),ID.eq(id))
    }
}

fun getRequestByThread(thread: Long): RequestRecord? {
    with (REQUEST) {
        return ctx.fetchOne(this, THREAD.eq(thread))
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

fun addSubscriber(user: Long, guild: Long, request: Int) {
    with (SUBSCRIPTION) {

        ctx.insertInto(this, USER, GUILD, REQUEST)
            .values(user, guild, request)
            .onDuplicateKeyIgnore()
            .execute()
    }
}

fun createInfo(request: RequestRecord, title: String, detail: String): RequestInfoRecord? {
    return createInfo(request.guild!!, request.id!!, title, detail)
}

fun createInfo(guild: Long, request: Int, title: String, detail: String): RequestInfoRecord? {
    with (REQUEST_INFO) {
        return ctx.insertInto(this, GUILD, REQUEST, TITLE, DETAIL)
            .values(guild, request, title, detail)
            .returning()
            .fetchOne()
    }
}

suspend fun fetchRequestFull(guild: Long, request: Int) = coroutineScope {

    with (REQUEST) {
        ctx.select(this, REQUEST_INFO).from(this)
            .join(REQUEST_INFO)
            .on(REQUEST_INFO.GUILD.eq(GUILD), REQUEST_INFO.REQUEST.eq(ID))
            .where(ID.eq(request), GUILD.eq(guild))
            .fetchOne()
    }
}

suspend fun editRequest(guild: Long, id: Int, title: String, detail: String) = coroutineScope {
    with (REQUEST_INFO) {
        ctx.update(this)
            .set(TITLE, title)
            .set(DETAIL, detail)
            .where(GUILD.eq(guild), REQUEST.eq(id))
            .returning()
            .fetchOne()
    }
}

suspend fun setRequestState(guild: Long, request: Int, state: State) = coroutineScope {

    with (REQUEST_INFO) {
        ctx.update(this)
            .set(STATE, state)
            .where(GUILD.eq(guild), REQUEST.eq(request))
            .returning()
            .fetchOne()
    }
}

fun fetchRequestInfo(guild: Long, request: Int): RequestInfoRecord? {
    with (REQUEST_INFO) {
        return ctx.fetchOne(this, GUILD.eq(guild), REQUEST.eq(request))
    }
}