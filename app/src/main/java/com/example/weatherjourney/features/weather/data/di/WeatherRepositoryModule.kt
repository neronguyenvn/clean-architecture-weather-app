package com.example.weatherjourney.features.weather.data.di

import android.content.Context
import com.example.weatherjourney.di.DefaultDispatcher
import com.example.weatherjourney.domain.AppPreferences
import com.example.weatherjourney.features.weather.data.local.AppDatabase
import com.example.weatherjourney.features.weather.data.remote.WeatherApi
import com.example.weatherjourney.features.weather.data.repository.DefaultLocationRepository
import com.example.weatherjourney.features.weather.data.repository.DefaultWeatherRepository
import com.example.weatherjourney.features.weather.domain.repository.LocationRepository
import com.example.weatherjourney.features.weather.domain.repository.WeatherRepository
import com.google.android.gms.location.FusedLocationProviderClient
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object WeatherRepositoryModule {

    @Provides
    @Singleton
    fun provideWeatherRepository(api: WeatherApi): WeatherRepository =
        DefaultWeatherRepository(api)

    @Provides
    @Singleton
    fun provideLocationRepository(
        api: WeatherApi,
        db: AppDatabase,
        client: FusedLocationProviderClient,
        appPreferences: AppPreferences,
        @ApplicationContext context: Context,
        @DefaultDispatcher defaultDispatcher: CoroutineDispatcher
    ): LocationRepository = DefaultLocationRepository(
        api = api,
        dao = db.locationDao(),
        client = client,
        appPreferences = appPreferences,
        context = context,
        defaultDispatcher = defaultDispatcher
    )
}
