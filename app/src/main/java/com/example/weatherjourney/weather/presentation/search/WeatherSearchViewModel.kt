package com.example.weatherjourney.weather.presentation.search

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherjourney.util.Result
import com.example.weatherjourney.util.UiEvent
import com.example.weatherjourney.util.UiText
import com.example.weatherjourney.weather.data.mapper.toCoordinate
import com.example.weatherjourney.weather.data.mapper.toSavedCity
import com.example.weatherjourney.weather.domain.usecase.LocationUseCases
import com.example.weatherjourney.weather.domain.usecase.WeatherUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val TAG = "WeatherSearchViewModel"

@HiltViewModel
class WeatherSearchViewModel @Inject constructor(
    private val locationUseCases: LocationUseCases,
    private val weatherUseCases: WeatherUseCases
) : ViewModel() {

    var uiState by mutableStateOf(WeatherSearchUiState())
        private set

    private val _uiEvent = MutableSharedFlow<UiEvent>()
    val uiEvent = _uiEvent.asSharedFlow()

    fun onEvent(event: WeatherSearchEvent) {
        when (event) {
            is WeatherSearchEvent.OnCityUpdate -> {
                uiState = uiState.copy(city = event.cityAddress)

                if (event.cityAddress.isNotBlank()) {
                    viewModelScope.launch {
                        when (
                            val suggestionCities =
                                locationUseCases.getSuggestionCities(event.cityAddress)
                        ) {
                            is Result.Success ->
                                uiState = uiState.copy(suggestionCities = suggestionCities.data)

                            is Result.Error -> handleError(suggestionCities)
                        }
                    }
                }
            }

            is WeatherSearchEvent.OnFetchWeatherOfSavedLocations -> fetchWeatherOfSavedLocations()
        }
    }

    private fun fetchWeatherOfSavedLocations() {
        viewModelScope.launch {
            var locations = locationUseCases.getLocations()
            val currentCoordinate = locationUseCases.getCurrentCoordinate()

            if (currentCoordinate is Result.Success) {
                val savedCurrentLocation = locations?.find { it.isCurrentLocation }

                if (savedCurrentLocation != null && savedCurrentLocation.toCoordinate() != currentCoordinate.data) {
                    launch { locationUseCases.deleteLocation(savedCurrentLocation) }

                    locationUseCases.getCityAddress(currentCoordinate.data)
                    locations = locationUseCases.getLocations()
                }
            }

            locations?.forEach {
                viewModelScope.launch {
                    when (
                        val weather =
                            weatherUseCases.getAllWeather(it.toCoordinate(), it.timeZone, true)
                    ) {
                        is Result.Success -> {
                            val newSavedCities = uiState.savedCities.toMutableList()

                            when (
                                val index =
                                    newSavedCities.indexOfFirst { city ->
                                        city.coordinate == it.toCoordinate()
                                    }
                            ) {
                                -1 -> newSavedCities.add(
                                    weather.data.toSavedCity(
                                        it.cityAddress,
                                        it.toCoordinate(),
                                        it.timeZone,
                                        it.isCurrentLocation
                                    )
                                )

                                else -> newSavedCities[index] =
                                    weather.data.toSavedCity(
                                        it.cityAddress,
                                        it.toCoordinate(),
                                        it.timeZone,
                                        it.isCurrentLocation
                                    )
                            }

                            uiState = uiState.copy(savedCities = newSavedCities)
                        }

                        is Result.Error -> handleError(weather)
                    }
                }
            }
        }
    }

    private suspend fun handleError(error: Result.Error) {
        val message = error.toString()
        Log.e(TAG, message)
        _uiEvent.emit(UiEvent.ShowSnackbar(UiText.DynamicString(message)))
    }
}
