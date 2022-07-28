package database

import ctx
import models.tables.records.RequestInfoRecord
import models.tables.records.RequestRecord
import models.tables.references.REQUEST
import models.tables.references.REQUEST_INFO
import models.tables.references.SUBSCRIPTION
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

fun addSubscriber(user: Long, guild: Long, request: Int, then: Consumer<Int>) {
    with (SUBSCRIPTION) {

        ctx.insertInto(this, USER, GUILD, REQUEST)
            .values(user, guild, request)
            .onDuplicateKeyIgnore()
            .executeAsync()
            .thenAccept(then)
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