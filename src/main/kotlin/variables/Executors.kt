package variables

import kotlinx.coroutines.asCoroutineDispatcher
import java.util.concurrent.Executors

/**
Handling events
 **/
val eventThread = Executors.newFixedThreadPool(50).asCoroutineDispatcher()