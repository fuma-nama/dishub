package service

import kotlinx.coroutines.CoroutineScope
import variables.serviceThread

interface Service : CoroutineScope {
    override val coroutineContext
    get() = serviceThread
}