package utils

import net.dv8tion.jda.api.requests.RestAction

fun combine(vararg actions: RestAction<*>): RestAction<*>? {
    var combined: RestAction<*>? = null

    for (action in actions) {
        combined = combined?.and(action) ?: action
    }

    return combined
}

fun combine(vararg actions: RestAction<*>, success: () -> Unit) {
    var combined: RestAction<*>? = null

    for (action in actions) {
        combined = combined?.and(action) ?: action
    }

    combined?.queue {
        success()
    }
}