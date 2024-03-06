package com.example.weatherjourney.core.data.di

import com.example.weatherjourney.core.data.GpsRepository
import com.example.weatherjourney.core.data.LocationRepository
import com.example.weatherjourney.core.data.NetworkMonitor
import com.example.weatherjourney.core.data.UserDataRepository
import com.example.weatherjourney.core.data.WeatherRepository
import com.example.weatherjourney.core.data.implementation.ConnectivityManagerNetworkMonitor
import com.example.weatherjourney.core.data.implementation.DefaultGpsRepository
import com.example.weatherjourney.core.data.implementation.OfflineFirstLocationRepository
import com.example.weatherjourney.core.data.implementation.OfflineFirstUserDataRepository
import com.example.weatherjourney.core.data.implementation.OfflineFirstWeatherRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class DataModule {

    @Binds
    abstract fun bindsLocationRepository(
        locationRepository: OfflineFirstLocationRepository
    ): LocationRepository

    @Binds
    abstract fun bindsWeatherRepository(
        weatherRepository: OfflineFirstWeatherRepository
    ): WeatherRepository

    @Binds
    abstract fun bindsUserDataRepository(
        userDataRepository: OfflineFirstUserDataRepository
    ): UserDataRepository

    @Binds
    abstract fun bindsGpsRepository(
        gpsRepository: DefaultGpsRepository
    ): GpsRepository

    @Binds
    abstract fun bindsNetworkMonitor(
        networkMonitor: ConnectivityManagerNetworkMonitor
    ): NetworkMonitor

}