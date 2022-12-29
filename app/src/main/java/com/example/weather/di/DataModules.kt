package com.example.weather.di

import com.example.weather.data.DefaultGeocodingRepository
import com.example.weather.data.DefaultLocationRepository
import com.example.weather.data.DefaultWeatherRepository
import com.example.weather.data.GeocodingRepository
import com.example.weather.data.LocationRepository
import com.example.weather.data.WeatherRepository
import com.example.weather.network.ApiService
import com.google.android.gms.location.FusedLocationProviderClient
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class RepositoryModule {

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

    @Singleton
    @Provides
    fun provideGeocodingRepository(
        apiService: ApiService,
        @IoDispatcher dispatcher: CoroutineDispatcher
    ): GeocodingRepository {
        return DefaultGeocodingRepository(apiService, dispatcher)
    }

    @Singleton
    @Provides
    fun provideLocationRepository(
        client: FusedLocationProviderClient,
        @DefaultDispatcher dispatcher: CoroutineDispatcher
    ): LocationRepository {
        return DefaultLocationRepository(client, dispatcher)
    }
}
