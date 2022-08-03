package database

import ctx
import kotlinx.coroutines.coroutineScope
import org.jooq.Record
import org.jooq.ResultQuery
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine
import models.tables.references.TODO as todo

fun saveTodos(user: Long, todos: Array<String?>) {
    ctx.insertInto(todo, todo.USER, todo.CONTENT)
        .values(user, todos)
        .onDuplicateKeyUpdate()
        .set(todo.CONTENT, todos)
        .executeAsync()
}

fun getTodos(user: Long): ArrayList<String>? {
    return ctx.selectFrom(todo)
        .where(todo.USER.eq(user))
        .fetchOne()?.content?.let {
            arrayListOf(*it) as ArrayList<String>?
        }
}

suspend fun getTodosAsync(user: Long) = suspendCoroutine { cont ->
    ctx.selectFrom(todo)
        .where(todo.USER.eq(user))
        .fetchAsync().thenAccept {
            val todos = it.firstOrNull()?.content

            cont.resume(todos)
        }
}

fun<R : Record?, T> ResultQuery<R>.fetchAsync(accept: (T) -> Unit, mapper: (org.jooq.Result<R>) -> T) {
    this.fetchAsync().thenAccept {
        accept(mapper(it))
    }
}

fun<T : Record?> ResultQuery<T>.fetchOneAsync(accept: (T) -> Unit) {
    this.fetchAsync().thenAccept {
        accept(it[0])
    }
}

fun<R : Record?, T> ResultQuery<R>.fetchOneAsync(accept: (T) -> Unit, mapper: (R) -> T) {
    this.fetchAsync().thenAccept {
        accept(mapper(it[0]))
    }
}