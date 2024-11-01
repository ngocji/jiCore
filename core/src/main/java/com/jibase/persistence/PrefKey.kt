package com.jibase.persistence

import androidx.datastore.preferences.core.Preferences

data class PrefKey(val key: Preferences.Key<*>, val isObjectValue: Boolean)