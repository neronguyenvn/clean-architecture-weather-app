package com.example.weatherjourney.features.weather.domain.di

import com.example.weatherjourney.domain.AppPreferences
import com.example.weatherjourney.features.weather.domain.repository.LocationRepository
import com.example.weatherjourney.features.weather.domain.repository.WeatherRepository
import com.example.weatherjourney.features.weather.domain.usecase.LocationUseCases
import com.example.weatherjourney.features.weather.domain.usecase.WeatherUseCases
import com.example.weatherjourney.features.weather.domain.usecase.location.ValidateCurrentLocation
import com.example.weatherjourney.features.weather.domain.usecase.weather.ConvertUnit
import com.example.weatherjourney.features.weather.domain.usecase.weather.GetAllWeather
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped

@Module
@InstallIn(ViewModelComponent::class)
class WeatherDomainModule {

    @Provides
    @ViewModelScoped
    fun provideLocationUseCases(
        repository: LocationRepository,
        preferences: AppPreferences,
    ): LocationUseCases =
        LocationUseCases(
            validateCurrentLocation = ValidateCurrentLocation(repository, preferences),
        )

    @Provides
    @ViewModelScoped
    fun provideWeatherUseCases(
        repository: WeatherRepository,
    ): WeatherUseCases = WeatherUseCases(
        getAllWeather = GetAllWeather(repository),
        convertUnit = ConvertUnit(),
    )
}
