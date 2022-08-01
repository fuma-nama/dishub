package variables

import com.github.benmanes.caffeine.cache.Scheduler
import kotlinx.coroutines.asCoroutineDispatcher
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledThreadPoolExecutor

/**
Handling events
 **/
val eventThread = Executors.newFixedThreadPool(10).asCoroutineDispatcher()

/**
 * For services to execute async processes
 */
val serviceThread = Executors.newFixedThreadPool(10).asCoroutineDispatcher()

/**
 * For cleaning listeners
 */
val cacheThread = Scheduler.forScheduledExecutorService(
    ScheduledThreadPoolExecutor(10)
)