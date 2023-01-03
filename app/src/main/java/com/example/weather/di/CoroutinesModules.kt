package com.example.weather.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import javax.inject.Qualifier

/**
 * Annotation for Default Coroutine dispatcher, used to clarify which dispatcher will be injected.
 */
@Qualifier
annotation class DefaultDispatcher

/**
 * Annotation for Io Coroutine dispatcher, used to clarify which dispatcher will be injected.
 */
@Qualifier
annotation class IoDispatcher

/**
 * Module for injecting Coroutine Dispatchers.
 */
@InstallIn(SingletonComponent::class)
@Module
class CoroutineDispatcherModule {

    /**
     * Inject Default Dispatcher.
     */
    @DefaultDispatcher
    @Provides
    fun providesDefaultDispatcher(): CoroutineDispatcher = Dispatchers.Default

    /**
     * Inject Io Dispatcher.
     */
    @IoDispatcher
    @Provides
    fun providesIoDispatcher(): CoroutineDispatcher = Dispatchers.IO
}
