package com.example.weather.di

import android.content.Context
import com.example.weather.data.DefaultGeocodingRepository
import com.example.weather.data.DefaultLocationRepository
import com.example.weather.data.DefaultWeatherRepository
import com.example.weather.data.GeocodingRepository
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
import javax.inject.Singleton

/**
 * Module for injecting Repositories
 */
@Module
@InstallIn(SingletonComponent::class)
class RepositoryModule {

    /**
     *  Inject Weather DataType Repository
     */
    @Singleton
    @Provides
    fun provideWeatherRepository(
        geocodingRepository: GeocodingRepository,
        locationRepository: LocationRepository,
        apiService: ApiService,
        @IoDispatcher dispatcher: CoroutineDispatcher
    ): WeatherRepository {
        return DefaultWeatherRepository(
            geocodingRepository,
            locationRepository,
            apiService,
            dispatcher
        )
    }

    /**
     * Inject Geocoding DataType Repository
     */
    @Singleton
    @Provides
    fun provideGeocodingRepository(
        apiService: ApiService,
        @IoDispatcher dispatcher: CoroutineDispatcher
    ): GeocodingRepository {
        return DefaultGeocodingRepository(apiService, dispatcher)
    }

    /**
     * Inject Location DataType Repository
     */
    @Singleton
    @Provides
    fun provideLocationRepository(
        client: FusedLocationProviderClient,
        @DefaultDispatcher dispatcher: CoroutineDispatcher
    ): LocationRepository {
        return DefaultLocationRepository(client, dispatcher)
    }
}

/**
 * Module for injecting Location Services
 */
@InstallIn(SingletonComponent::class)
@Module
class LocationModule {

    /**
     *  Inject FusedLocationProviderClient used to get Current Location
     */
    @Singleton
    @Provides
    fun provideFusedLocationProviderClient(
        @ApplicationContext context: Context
    ): FusedLocationProviderClient {
        return LocationServices.getFusedLocationProviderClient(context)
    }
}
