package com.example.weatherjourney.weather.data.di

import com.example.weatherjourney.di.DefaultDispatcher
import com.example.weatherjourney.weather.data.repository.DefaultLocationRepository
import com.example.weatherjourney.weather.data.repository.DefaultWeatherRepository
import com.example.weatherjourney.weather.data.source.LocationDataSource
import com.example.weatherjourney.weather.data.source.remote.ApiService
import com.example.weatherjourney.weather.domain.repository.LocationRepository
import com.example.weatherjourney.weather.domain.repository.WeatherRepository
import com.google.android.gms.location.FusedLocationProviderClient
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    @Singleton
    fun provideWeatherRepository(apiService: ApiService): WeatherRepository =
        DefaultWeatherRepository(apiService)

    @Provides
    @Singleton
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
}
