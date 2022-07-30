package variables

import kotlinx.coroutines.asCoroutineDispatcher
import java.util.concurrent.Executors

/**
Handling events
 **/
val eventThread = Executors.newFixedThreadPool(10).asCoroutineDispatcher()

/**
 * For services to execute async processes
 */
val serviceThread = Executors.newFixedThreadPool(10).asCoroutineDispatcher()