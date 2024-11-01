package com.jibase.extensions

import kotlinx.coroutines.cancel
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow

fun timer(period: Long, max: Long = -1, initialDelay: Long = 0, action: (Long, Boolean) -> Unit) =
    flow {
        var total = initialDelay

        delay(initialDelay)
        while (true) {
            action.invoke(total, false)
            emit(Unit)
            delay(period)
            total += period
            if (max != -1L && total >= max) {
                currentCoroutineContext().cancel()
                action.invoke(total, true)
            }
        }
    }