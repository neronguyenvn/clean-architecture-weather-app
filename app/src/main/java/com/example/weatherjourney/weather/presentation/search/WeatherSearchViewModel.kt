package com.example.weatherjourney.weather.presentation.search

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
import com.example.weatherjourney.weather.data.source.local.entity.LocationEntity
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
        viewModelScope.launch {
            val locations: List<LocationEntity> = locationRepository.getLocations()
            locations.forEach {
                fetchWeatherOfSavedLocations(it.city, it.toCoordinate())
            }
        }
    }

    private fun fetchWeatherOfSavedLocations(city: String, coordinate: Coordinate) {
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
                        -1 -> newSavedCities.add(currentWeather.toSavedCity(city, coordinate))
                        else -> newSavedCities[index] = currentWeather.toSavedCity(city, coordinate)
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
