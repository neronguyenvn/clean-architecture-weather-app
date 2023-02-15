package com.example.weatherjourney.weather.domain.di

import com.example.weatherjourney.di.IoDispatcher
import com.example.weatherjourney.domain.PreferenceRepository
import com.example.weatherjourney.weather.domain.repository.LocationRepository
import com.example.weatherjourney.weather.domain.repository.WeatherRepository
import com.example.weatherjourney.weather.domain.usecase.DeleteLocation
import com.example.weatherjourney.weather.domain.usecase.GetAllWeather
import com.example.weatherjourney.weather.domain.usecase.GetCityAddress
import com.example.weatherjourney.weather.domain.usecase.GetCurrentCoordinate
import com.example.weatherjourney.weather.domain.usecase.GetLocations
import com.example.weatherjourney.weather.domain.usecase.GetSuggestionCities
import com.example.weatherjourney.weather.domain.usecase.LocationUseCases
import com.example.weatherjourney.weather.domain.usecase.SaveLocation
import com.example.weatherjourney.weather.domain.usecase.ShouldSaveLocation
import com.example.weatherjourney.weather.domain.usecase.ValidateLastInfo
import com.example.weatherjourney.weather.domain.usecase.WeatherUseCases
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.CoroutineDispatcher

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
            getCurrentCoordinate = GetCurrentCoordinate(repository),
            validateLastInfo = ValidateLastInfo(),
            getCityAddress = GetCityAddress(repository),
            getSuggestionCities = GetSuggestionCities(repository),
            getLocations = GetLocations(repository),
            deleteLocation = DeleteLocation(repository)
        )

    @Provides
    @ViewModelScoped
    fun provideWeatherUseCases(
        repository: WeatherRepository,
        preferences: PreferenceRepository,
        @IoDispatcher ioDispatcher: CoroutineDispatcher
    ): WeatherUseCases = WeatherUseCases(
        getAllWeather = GetAllWeather(repository, preferences, ioDispatcher)
    )
}
