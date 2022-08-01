package utils

import bjda.ui.core.UI
import com.github.benmanes.caffeine.cache.Cache
import com.github.benmanes.caffeine.cache.Caffeine
import variables.cacheThread
import java.util.concurrent.TimeUnit

fun<K> CacheMap(): Cache<K, UI> {

    return Caffeine.newBuilder()
        .maximumSize(200000)
        .scheduler(cacheThread)
        .expireAfterWrite(10, TimeUnit.MINUTES)
        .removalListener<K, UI> { _, ui, _ ->
            ui?.destroy()
        }
        .build()
}

open class UIStore<T: Any> : Cache<T, UI> by CacheMap() {

    operator fun get(key: T): UI? {

        return this.getIfPresent(key)
    }

    operator fun set(key: T, value: UI) {
        this.put(key, value)
    }
}