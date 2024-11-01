package com.jibase.flow

sealed class ResultWrapper<out T> {

    data class Success<out T>(val value: T) : ResultWrapper<T>()

    data object Empty : ResultWrapper<Nothing>()

    data class Error(val throwable: Throwable? = null) : ResultWrapper<Nothing>()

    data object None : ResultWrapper<Nothing>()

    data object Loading : ResultWrapper<Nothing>()

    @Throws(Exception::class)
    fun takeValueOrThrow(): T {
        return when (this) {
            is Success -> value
            is Error -> throw throwable ?: Throwable()
            else -> throw Throwable("Unknown the result type")
        }
    }

    fun safeTakeValue(): T? {
        return (this as? Success<T>)?.value
    }
}