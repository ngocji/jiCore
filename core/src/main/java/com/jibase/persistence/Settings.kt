@file:Suppress("unused")

package com.jibase.persistence

import android.content.Context
import dagger.hilt.android.EntryPointAccessors
import kotlinx.coroutines.launch

data class Setting<T : Any>(val key: String, val default: T)

inline fun <reified T : Any> Setting<T>.readBlocking(context: Context) =
    EntryPointAccessors.fromApplication(context, PersistenceEntryPoint::class.java)
        .persistent()
        .blockingRead(key, default)


inline fun <reified T : Any> Setting<T>.read(context: Context) =
    EntryPointAccessors.fromApplication(context, PersistenceEntryPoint::class.java)
        .persistent()
        .read(key, default)


inline fun <reified T : Any> Setting<T>.save(context: Context, value: T) {
    val entryPoint = EntryPointAccessors.fromApplication(context, PersistenceEntryPoint::class.java)
    entryPoint.coroutine().launch {
        entryPoint.persistent().save(key, value)
    }
}

inline fun <reified T : Any> Setting<T>.remove(context: Context) {
    val entryPoint = EntryPointAccessors.fromApplication(context, PersistenceEntryPoint::class.java)
    entryPoint.coroutine().launch {
        entryPoint.persistent().remove<T>(key)
    }
}

