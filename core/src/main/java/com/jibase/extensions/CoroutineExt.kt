package com.jibase.extensions

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.launch

typealias SuspendActionUnit = suspend CoroutineScope.() -> Unit

fun CoroutineScope.runAsync(
    context: CoroutineDispatcher = Dispatchers.IO,
    onComplete: (suspend CoroutineScope.(isCompleted: Boolean) -> Unit)? = null,
    vararg actions: SuspendActionUnit
): Job {
    return launch(context = context) {
        val deferredJobs = actions.map { action ->
            async {
                action()
            }
        }
        var isCompleted = false
        try {
            deferredJobs.awaitAll()
            isCompleted = true
        } catch (_: Exception) {

        } finally {
            if (onComplete != null) onComplete(isCompleted)
        }
    }
}

fun CoroutineScope.runCoroutine(
    context: CoroutineDispatcher = Dispatchers.IO,
    action: SuspendActionUnit
) = launch(context = context) { action() }

fun <T> Flow<T>.cachedIn(scope: CoroutineScope) = this
    .shareIn(
        scope,
        replay = 1,
        started = SharingStarted.Lazily
    )