package com.example.weatherjourney.features.weather.domain.di

import com.example.weatherjourney.domain.AppPreferences
import com.example.weatherjourney.features.weather.domain.repository.LocationRepository
import com.example.weatherjourney.features.weather.domain.repository.WeatherRepository
import com.example.weatherjourney.features.weather.domain.usecase.LocationUseCases
import com.example.weatherjourney.features.weather.domain.usecase.WeatherUseCases
import com.example.weatherjourney.features.weather.domain.usecase.location.DeleteLocation
import com.example.weatherjourney.features.weather.domain.usecase.location.GetLocationsStream
import com.example.weatherjourney.features.weather.domain.usecase.location.GetSuggestionCities
import com.example.weatherjourney.features.weather.domain.usecase.location.SaveLocation
import com.example.weatherjourney.features.weather.domain.usecase.location.ShouldSaveLocation
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
        appPreferences: AppPreferences
    ): LocationUseCases =
        LocationUseCases(
            saveLocation = SaveLocation(repository),
            shouldSaveLocation = ShouldSaveLocation(repository, appPreferences),
            validateCurrentLocation = ValidateCurrentLocation(repository),
            getSuggestionCities = GetSuggestionCities(repository),
            getLocationsStream = GetLocationsStream(repository),
            deleteLocation = DeleteLocation(repository)
        )

    @Provides
    @ViewModelScoped
    fun provideWeatherUseCases(
        repository: WeatherRepository
    ): WeatherUseCases = WeatherUseCases(
        getAllWeather = GetAllWeather(repository),
        convertUnit = ConvertUnit()
    )
}
