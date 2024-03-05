package com.example.weatherjourney.core.data.di

import com.example.weatherjourney.core.data.LocationRepository
import com.example.weatherjourney.core.data.NetworkMonitor
import com.example.weatherjourney.core.data.UserDataRepository
import com.example.weatherjourney.core.data.WeatherRepository
import com.example.weatherjourney.core.data.implementation.ConnectivityManagerNetworkMonitor
import com.example.weatherjourney.core.data.implementation.DefaultLocationRepository
import com.example.weatherjourney.core.data.implementation.DefaultWeatherRepository
import com.example.weatherjourney.core.data.implementation.OfflineFirstUserDataRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class DataModule {

    @Binds
    abstract fun bindsLocationRepository(
        locationRepository: DefaultLocationRepository
    ): LocationRepository

    @Binds
    abstract fun bindsWeatherRepository(
        weatherRepository: DefaultWeatherRepository
    ): WeatherRepository

    @Binds
    abstract fun bindsUserDataRepository(
        userDataRepository: OfflineFirstUserDataRepository
    ): UserDataRepository

    @Binds
    abstract fun bindsNetworkMonitor(
        networkMonitor: ConnectivityManagerNetworkMonitor
    ): NetworkMonitor
}