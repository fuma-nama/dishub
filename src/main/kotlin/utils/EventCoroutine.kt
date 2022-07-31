package utils

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import net.dv8tion.jda.api.interactions.InteractionHook
import net.dv8tion.jda.api.interactions.callbacks.IReplyCallback
import variables.eventThread

interface EventCoroutine: CoroutineScope {
    override val coroutineContext
        get() = eventThread

    fun IReplyCallback.later(ephemeral: Boolean = false, block: suspend CoroutineScope.(InteractionHook) -> Unit) {
        deferReply(ephemeral).queue { hook ->
            launch { block(hook) }
        }
    }
}