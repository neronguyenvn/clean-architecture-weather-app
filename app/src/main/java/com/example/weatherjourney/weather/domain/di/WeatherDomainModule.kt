package com.example.weatherjourney.weather.domain.di

import com.example.weatherjourney.domain.PreferenceRepository
import com.example.weatherjourney.weather.domain.repository.LocationRepository
import com.example.weatherjourney.weather.domain.repository.WeatherRepository
import com.example.weatherjourney.weather.domain.usecase.LocationUseCases
import com.example.weatherjourney.weather.domain.usecase.WeatherUseCases
import com.example.weatherjourney.weather.domain.usecase.location.DeleteLocation
import com.example.weatherjourney.weather.domain.usecase.location.GetLocationsStream
import com.example.weatherjourney.weather.domain.usecase.location.GetSuggestionCities
import com.example.weatherjourney.weather.domain.usecase.location.SaveLocation
import com.example.weatherjourney.weather.domain.usecase.location.ShouldSaveLocation
import com.example.weatherjourney.weather.domain.usecase.location.ValidateCurrentLocation
import com.example.weatherjourney.weather.domain.usecase.weather.ConvertUnit
import com.example.weatherjourney.weather.domain.usecase.weather.GetAllWeather
import com.example.weatherjourney.weather.domain.usecase.weather.GetWeatherNotifications
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
        preferences: PreferenceRepository
    ): LocationUseCases =
        LocationUseCases(
            saveLocation = SaveLocation(repository),
            shouldSaveLocation = ShouldSaveLocation(repository, preferences),
            validateCurrentLocation = ValidateCurrentLocation(repository),
            getSuggestionCities = GetSuggestionCities(repository),
            getLocationsStream = GetLocationsStream(repository),
            deleteLocation = DeleteLocation(repository)
        )

    @Provides
    @ViewModelScoped
    fun provideWeatherUseCases(
        repository: WeatherRepository,
        preferences: PreferenceRepository
    ): WeatherUseCases = WeatherUseCases(
        getAllWeather = GetAllWeather(repository),
        getWeatherNotifications = GetWeatherNotifications(repository, preferences),
        convertUnit = ConvertUnit()
    )
}
