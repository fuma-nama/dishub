package utils

import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.interactions.InteractionHook
import net.dv8tion.jda.api.requests.RestAction
import java.util.concurrent.TimeUnit
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

fun combine(first: RestAction<*>, vararg actions: RestAction<*>): RestAction<*> {
    var combined: RestAction<*> = first

    for (action in actions) {
        combined = combined.and(action) ?: action
    }

    return combined
}

operator fun RestAction<*>.plus(action: RestAction<*>): RestAction<Void> {
    return this.and(action)
}

suspend fun<T> RestAction<T>.queueAsync() = suspendCoroutine { cont ->
    queue {
        cont.resume(it)
    }
}

fun RestAction<InteractionHook>.deleteLater() {
    queue {
        it.deleteOriginal().queueAfter(3, TimeUnit.SECONDS)
    }
}

fun Message.deleteLater() {
    this.delete().queueAfter(3, TimeUnit.SECONDS)
}
