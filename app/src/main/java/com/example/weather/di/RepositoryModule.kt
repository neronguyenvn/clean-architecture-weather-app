package com.example.weather.di

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.example.weather.data.DefaultLocationRepository
import com.example.weather.data.DefaultPreferenceRepository
import com.example.weather.data.DefaultWeatherRepository
import com.example.weather.data.LocationDataSource
import com.example.weather.data.LocationRepository
import com.example.weather.data.PreferenceRepository
import com.example.weather.data.WeatherRepository
import com.example.weather.network.ApiService
import com.google.android.gms.location.FusedLocationProviderClient
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Singleton

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
    fun provideWeatherRepository(apiService: ApiService): WeatherRepository =
        DefaultWeatherRepository(apiService)

    /**
     * Inject Location DataType Repository.
     */
    @Singleton
    @Provides
    fun provideLocationRepository(
        @RemoteLocationDataSource remoteDataSource: LocationDataSource,
        @LocalLocationDataSource localDataSource: LocationDataSource,
        client: FusedLocationProviderClient,
        @DefaultDispatcher defaultDispatcher: CoroutineDispatcher
    ): LocationRepository = DefaultLocationRepository(
        locationRemoteDataSource = remoteDataSource,
        locationLocalDataSource = localDataSource,
        client = client,
        defaultDispatcher = defaultDispatcher
    )

    /**
     * Inject Preference DataType Repository
     */
    @Singleton
    @Provides
    fun providePreferenceRepository(dataStore: DataStore<Preferences>): PreferenceRepository =
        DefaultPreferenceRepository(dataStore)
}
