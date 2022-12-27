package com.example.weather.di

import com.example.weather.data.DefaultGeocodingRepository
import com.example.weather.data.DefaultWeatherRepository
import com.example.weather.data.GeocodingRepository
import com.example.weather.data.WeatherRepository
import com.example.weather.network.ApiService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class RepositoryModule {

    @Singleton
    @Provides
    fun provideWeatherRepository(
        geocodingRepository: GeocodingRepository,
        apiService: ApiService
    ): WeatherRepository {
        return DefaultWeatherRepository(geocodingRepository, apiService)
    }

    @Singleton
    @Provides
    fun provideGeocodingRepository(
        apiService: ApiService
    ): GeocodingRepository {
        return DefaultGeocodingRepository(apiService)
    }
}