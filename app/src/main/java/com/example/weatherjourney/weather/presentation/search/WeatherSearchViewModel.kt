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
import com.example.weatherjourney.weather.data.mapper.toCurrentWeather
import com.example.weatherjourney.weather.domain.mapper.toSavedCity
import com.example.weatherjourney.weather.domain.model.Coordinate
import com.example.weatherjourney.weather.domain.model.CurrentWeather
import com.example.weatherjourney.weather.domain.repository.LocationRepository
import com.example.weatherjourney.weather.domain.repository.WeatherRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val TAG = "WeatherSearchViewModel"

@HiltViewModel
class WeatherSearchViewModel @Inject constructor(
    private val locationRepository: LocationRepository,
    private val weatherRepository: WeatherRepository
) : ViewModel() {

    var uiState by mutableStateOf(WeatherSearchUiState())
        private set

    private val _uiEvent = Channel<UiEvent>()
    val uiEvent = _uiEvent.receiveAsFlow()

    fun onEvent(event: WeatherSearchEvent) {
        when (event) {
            is WeatherSearchEvent.OnCityUpdate -> {
                uiState = uiState.copy(city = event.city)
                fetchSuggestionCities(event.city)
            }

            is WeatherSearchEvent.OnFetchWeatherOfSavedLocations -> fetchWeatherOfSavedLocations()
        }
    }

    private fun fetchSuggestionCities(city: String) {
        if (city.isNotBlank()) {
            viewModelScope.launch {
                when (val suggestionCities = locationRepository.fetchSuggestionLocations(city)) {
                    is Result.Success ->
                        uiState =
                            uiState.copy(suggestionCities = suggestionCities.data)

                    is Result.Error -> {
                        val message = suggestionCities.toString()
                        _uiEvent.send(UiEvent.ShowSnackbar(UiText.DynamicString(message)))
                    }
                }
            }
        }
    }

    private fun fetchWeatherOfSavedLocations() {
        Log.d(TAG, "fetchWeatherOfSavedLocations() called")
        viewModelScope.launch {
            var locations = locationRepository.getLocations()

            val currentCoordinate = locationRepository.getCurrentCoordinate()
            if (currentCoordinate is Result.Success) {
                val savedCurrentLocation = locations.find { it.isCurrentLocation }
                if (savedCurrentLocation != null && savedCurrentLocation.toCoordinate() != currentCoordinate.data) {
                    launch { locationRepository.deleteLocation(savedCurrentLocation) }
                    locationRepository.getCityByCoordinate(currentCoordinate.data, true)
                    locations = locationRepository.getLocations()
                }
            }

            locations.forEach {
                fetchWeatherOfSavedLocation(it.city, it.toCoordinate(), it.isCurrentLocation)
            }
        }
    }

    private fun fetchWeatherOfSavedLocation(
        city: String,
        coordinate: Coordinate,
        isCurrentLocation: Boolean
    ) {
        viewModelScope.launch {
            when (val weather = weatherRepository.fetchAllWeather(coordinate)) {
                is Result.Success -> {
                    val currentWeather: CurrentWeather
                    weather.data.apply {
                        currentWeather = current.toCurrentWeather(
                            timezoneOffset,
                            hourly.first().precipitationChance
                        )
                    }

                    val newSavedCities = uiState.savedCities.toMutableList()

                    when (val index = newSavedCities.indexOfFirst { it.coordinate == coordinate }) {
                        -1 -> newSavedCities.add(
                            currentWeather.toSavedCity(
                                city,
                                coordinate,
                                isCurrentLocation
                            )
                        )

                        else -> newSavedCities[index] =
                            currentWeather.toSavedCity(city, coordinate, isCurrentLocation)
                    }

                    uiState = uiState.copy(savedCities = newSavedCities)
                }

                is Result.Error -> {
                    val message = weather.toString()
                    _uiEvent.send(UiEvent.ShowSnackbar(UiText.DynamicString(message)))
                }
            }
        }
    }
}
