package com.example.weather.di

import com.example.weather.data.*
import com.example.weather.network.ApiService
import com.google.android.gms.location.FusedLocationProviderClient
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class RepositoryModule {

    @Singleton
    @Provides
    fun provideWeatherRepository(
        geocodingRepository: GeocodingRepository,
        locationRepository: LocationRepository,
        apiService: ApiService
    ): WeatherRepository {
        return DefaultWeatherRepository(geocodingRepository, locationRepository, apiService)
    }

    @Singleton
    @Provides
    fun provideGeocodingRepository(
        apiService: ApiService
    ): GeocodingRepository {
        return DefaultGeocodingRepository(apiService)
    }

    @Singleton
    @Provides
    fun provideLocationRepository(
        dataSource: LocationDataSource,
        @ApplicationScope externalScope: CoroutineScope
    ): LocationRepository {
        return DefaultLocationRepository(dataSource, externalScope)
    }

    @Singleton
    @Provides
    fun provideLocationDataSource(client: FusedLocationProviderClient): LocationDataSource {
        return DefaultLocationDataSource(client)
    }
}