package com.example.weatherjourney.weather.data.di

import com.example.weatherjourney.di.DefaultDispatcher
import com.example.weatherjourney.di.IoDispatcher
import com.example.weatherjourney.domain.PreferenceRepository
import com.example.weatherjourney.weather.data.local.AppDatabase
import com.example.weatherjourney.weather.data.remote.Api
import com.example.weatherjourney.weather.data.repository.DefaultLocationRepository
import com.example.weatherjourney.weather.data.repository.DefaultWeatherRepository
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
    fun provideWeatherRepository(api: Api): WeatherRepository =
        DefaultWeatherRepository(api)

    @Provides
    @Singleton
    fun provideLocationRepository(
        api: Api,
        db: AppDatabase,
        client: FusedLocationProviderClient,
        preferences: PreferenceRepository,
        @DefaultDispatcher defaultDispatcher: CoroutineDispatcher,
        @IoDispatcher ioDispatcher: CoroutineDispatcher
    ): LocationRepository = DefaultLocationRepository(
        api = api,
        dao = db.locationDao(),
        client = client,
        preferences = preferences,
        defaultDispatcher = defaultDispatcher,
        ioDispatcher = ioDispatcher
    )
}
