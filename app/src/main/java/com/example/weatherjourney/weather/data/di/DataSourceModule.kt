package com.example.weatherjourney.weather.data.di

import com.example.weatherjourney.weather.data.source.LocationDataSource
import com.example.weatherjourney.weather.data.source.local.AppDatabase
import com.example.weatherjourney.weather.data.source.local.LocationLocalDataSource
import com.example.weatherjourney.weather.data.source.remote.ApiService
import com.example.weatherjourney.weather.data.source.remote.LocationRemoteDataSource
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Qualifier
import javax.inject.Singleton

@Qualifier
annotation class RemoteLocationDataSource

@Qualifier
annotation class LocalLocationDataSource

@Module
@InstallIn(SingletonComponent::class)
object DataSourceModule {

    @Provides
    @Singleton
    @RemoteLocationDataSource
    fun provideLocationRemoteSource(apiService: ApiService): LocationDataSource {
        return LocationRemoteDataSource(apiService)
    }

    @Provides
    @Singleton
    @LocalLocationDataSource
    fun provideLocationLocalSource(database: AppDatabase): LocationDataSource {
        return LocationLocalDataSource(database.locationDao())
    }
}
