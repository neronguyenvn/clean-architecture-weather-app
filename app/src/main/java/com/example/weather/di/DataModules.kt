package com.example.weather.di

import android.content.Context
import androidx.room.Room
import com.example.weather.data.AppDatabase
import com.example.weather.data.DefaultLocationRepository
import com.example.weather.data.DefaultWeatherRepository
import com.example.weather.data.LocationDataSource
import com.example.weather.data.LocationLocalDataSource
import com.example.weather.data.LocationRemoteDataSource
import com.example.weather.data.LocationRepository
import com.example.weather.data.WeatherRepository
import com.example.weather.network.ApiService
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
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

/**
 * Module for injecting Repositories.
 */
@Module
@InstallIn(SingletonComponent::class)
class RepositoryModule {

    /**
     *  Inject Weather DataType Repository.
     */
    @Singleton
    @Provides
    fun provideWeatherRepository(
        apiService: ApiService,
        @IoDispatcher dispatcher: CoroutineDispatcher
    ): WeatherRepository {
        return DefaultWeatherRepository(apiService, dispatcher)
    }

    /**
     * Inject Location DataType Repository.
     */
    @Singleton
    @Provides
    fun provideLocationRepository(
        @RemoteLocationDataSource remoteDataSource: LocationDataSource,
        @LocalLocationDataSource localDataSource: LocationDataSource,
        client: FusedLocationProviderClient,
        @DefaultDispatcher defaultDispatcher: CoroutineDispatcher,
        @IoDispatcher ioDispatcher: CoroutineDispatcher
    ): LocationRepository {
        return DefaultLocationRepository(
            locationRemoteDataSource = remoteDataSource,
            locationLocalDataSource = localDataSource,
            client = client,
            defaultDispatcher = defaultDispatcher,
            ioDispatcher = ioDispatcher
        )
    }
}

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
