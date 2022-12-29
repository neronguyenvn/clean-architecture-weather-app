package com.example.weather.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import javax.inject.Qualifier

@Qualifier
annotation class DefaultDispatcher

@Qualifier
annotation class IoDispatcher

/**
 * Module for injecting coroutine dispatchers
 */
@InstallIn(SingletonComponent::class)
@Module
class CoroutineDispatcherModule {

    // Function for injecting default dispatcher
    @DefaultDispatcher
    @Provides
    fun providesDefaultDispatcher(): CoroutineDispatcher = Dispatchers.Default

    // Function for injecting io dispatcher
    @IoDispatcher
    @Provides
    fun providesIoDispatcher(): CoroutineDispatcher = Dispatchers.IO
}
