package com.example.weather.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.core.handlers.ReplaceFileCorruptionHandler
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.preferencesDataStoreFile
import androidx.room.Room
import com.example.weather.data.ApiService
import com.example.weather.data.AppDatabase
import com.example.weather.data.LocationDataSource
import com.example.weather.data.LocationLocalDataSource
import com.example.weather.data.LocationRemoteDataSource
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Qualifier
import javax.inject.Singleton

/**
 * Annotation for Location Data Source, used to clarify Remote source will be injected.
 */
@Qualifier
annotation class RemoteLocationDataSource

/**
 * Annotation for Location Data Source, used to clarify Local source will be injected.
 */
@Qualifier
annotation class LocalLocationDataSource

private const val USER_PREFERENCES = "user_preferences"

/**
 * Module for inject Data Sources.
 */
@InstallIn(SingletonComponent::class)
@Module
class DataSourceModule {

    /**
     * Inject Location Remote Data Source.
     */
    @Singleton
    @RemoteLocationDataSource
    @Provides
    fun provideLocationRemoteSource(apiService: ApiService): LocationDataSource {
        return LocationRemoteDataSource(apiService)
    }

    /**
     * Inject Location Local Data Source.
     */
    @Singleton
    @LocalLocationDataSource
    @Provides
    fun provideLocationLocalSource(database: AppDatabase): LocationDataSource {
        return LocationLocalDataSource(database.locationDao())
    }
}

/**
 * Module for injecting Room Database instance.
 */
@InstallIn(SingletonComponent::class)
@Module
class DatabaseModule {

    /**
     * Inject Room database.
     */
    @Singleton
    @Provides
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context.applicationContext,
            AppDatabase::class.java,
            "WeatherApp.db"
        ).build()
    }
}

/**
 * Module for injecting Location Services.
 */
@InstallIn(SingletonComponent::class)
@Module
class LocationModule {

    /**
     *  Inject FusedLocationProviderClient used to get Current Location.
     */
    @Singleton
    @Provides
    fun provideFusedLocationProviderClient(
        @ApplicationContext context: Context
    ): FusedLocationProviderClient {
        return LocationServices.getFusedLocationProviderClient(context)
    }
}

/**
 * Module inject DataStore instances
 */
@InstallIn(SingletonComponent::class)
@Module
class DataStoreModule {

    /**
     * Inject Preferences DataStore instace
     */
    @Singleton
    @Provides
    fun providePreferencesDataStore(@ApplicationContext context: Context): DataStore<Preferences> =
        PreferenceDataStoreFactory.create(
            corruptionHandler = ReplaceFileCorruptionHandler(produceNewData = { emptyPreferences() }),
            produceFile = { context.preferencesDataStoreFile(USER_PREFERENCES) }
        )
}
