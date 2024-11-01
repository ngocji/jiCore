package com.jibase.flow

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.withContext
import timber.log.Timber

inline fun <T> ResultWrapper<T>.applyWhenSuccess(action: (value: T) -> Unit): ResultWrapper<T> {
    if (this is ResultWrapper.Success) {
        action.invoke(value ?: return this)
    }
    return this
}

inline fun <T, R> ResultWrapper<T>.mapWhenSuccess(action: (value: T) -> R): ResultWrapper<R> {
    return when (this) {
        is ResultWrapper.Loading -> ResultWrapper.Loading
        is ResultWrapper.Error -> ResultWrapper.Error(this.throwable)
        is ResultWrapper.Success -> ResultWrapper.Success(
            action(
                value ?: throw IllegalArgumentException("Data is null")
            )
        )

        else -> ResultWrapper.None
    }
}

suspend fun <T> safeFlowCall(
    action: suspend () -> T?
) = withContext(Dispatchers.IO) {
    try {
        action.invoke()
    } catch (e: Exception) {
        Timber.d("Error flow call: $e")
        null
    }
}

suspend fun <T> safeFlowCall(
    error: (suspend () -> T?)? = null,
    action: suspend () -> T?,
) = withContext(Dispatchers.IO) {
    try {
        action.invoke()
    } catch (e: Exception) {
        Timber.d("Error flow call: $e")
        error?.invoke()
    }
}

suspend fun <T> safeApiCall(
    error: suspend (throwable: Throwable) -> ResultWrapper<T> = { ResultWrapper.Error(throwable = it) },
    apiCall: suspend () -> T
): ResultWrapper<T> {
    return withContext(Dispatchers.IO) {
        try {
            val data = apiCall.invoke()
            ResultWrapper.Success(data)
        } catch (throwable: Throwable) {
            Timber.d("Error safeApi: ${throwable.message}")
            error(throwable)
        }
    }
}

@OptIn(ExperimentalCoroutinesApi::class)
fun <T> safeFlow(
    flow: Flow<T>
): Flow<ResultWrapper<T>> =
    flow.mapLatest {
        ResultWrapper.Success(it)
    }
        .catch { ResultWrapper.Error(it) }