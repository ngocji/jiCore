package com.jibase.persistence

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.doublePreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.jibase.helper.GsonManager
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class Persistence @Inject constructor(
    @ApplicationContext val context: Context,
    @PersistenceName val persistenceName: String
) {
    val Context.dataStore by preferencesDataStore(name = persistenceName)

    suspend inline fun <reified T : Any> save(key: String, value: T) {
        context.dataStore.edit { settings ->
            when (T::class) {
                Int::class -> {
                    settings[intPreferencesKey(key)] = value as Int
                }

                Long::class -> {
                    settings[longPreferencesKey(key)] = value as Long
                }

                Boolean::class -> {
                    settings[booleanPreferencesKey(key)] = value as Boolean
                }

                Double::class -> {
                    settings[doublePreferencesKey(key)] = value as Double
                }

                Float::class -> {
                    settings[floatPreferencesKey(key)] = value as Float
                }

                String::class -> {
                    settings[stringPreferencesKey(key)] = value as String
                }

                else -> {
                    settings[stringPreferencesKey(key)] = try {
                        GsonManager.toJson(value)
                    } catch (e: Exception) {
                        e.printStackTrace()
                        ""
                    }
                }
            }
        }

    }

    inline fun <reified T : Any> read(key: String, defaultValue: T): Flow<T> {
        val (prefKey, isObjectValue) = getPrefKey<T>(key)
        return context.dataStore.data.map {
            it[prefKey]?.let { value ->
                convertObjectValueIfNeed(value, isObjectValue)
            } ?: defaultValue
        } as? Flow<T> ?: flowOf(defaultValue)
    }

    suspend inline fun <reified T : Any> remove(key: String) {
        context.dataStore.edit { settings ->
            val prefKey = getPrefKey<T>(key).key
            settings.remove(prefKey)
        }
    }

    inline fun <reified T : Any> blockingRead(key: String, defaultValue: T): T {
        val (prefKey, isObjectValue) = getPrefKey<T>(key)

        val value = runBlocking {
            context.dataStore.data.map {
                it[prefKey]?.let { value ->
                    convertObjectValueIfNeed(value, isObjectValue)
                } ?: defaultValue
            }.first()
        }

        return (value as? T) ?: defaultValue
    }

    inline fun <reified T : Any> convertObjectValueIfNeed(value: Any, isObjectValue: Boolean): T? {
        return if (isObjectValue) {
            try {
                GsonManager.fromJson<T>(value.toString(), T::class.java)
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        } else {
            value as? T
        }
    }

    inline fun <reified T : Any> getPrefKey(key: String): PrefKey {
        var isObjectValue = false

        val prefKey = when (T::class) {
            Int::class -> intPreferencesKey(key)
            Long::class -> longPreferencesKey(key)
            Boolean::class -> booleanPreferencesKey(key)
            Double::class -> doublePreferencesKey(key)
            Float::class -> floatPreferencesKey(key)
            String::class -> stringPreferencesKey(key)
            else -> stringPreferencesKey(key).also {
                isObjectValue = true
            }
        }

        return PrefKey(
            key = prefKey,
            isObjectValue = isObjectValue
        )
    }
}
