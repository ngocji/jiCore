package com.jibase.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import com.jibase.persistence.PersistenceName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class CoreModule {
    @Provides
    @Singleton
    @PersistenceName
    fun providerDefaultSharePrefHelper(): String {
        return "app.ds"
    }

    @Singleton
    @Provides
    fun provideCoroutineScope() = CoroutineScope(Dispatchers.IO)
}