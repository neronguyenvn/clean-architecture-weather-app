package com.example.weatherjourney.weather.presentation.search

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherjourney.R
import com.example.weatherjourney.util.Result
import com.example.weatherjourney.util.UiEvent
import com.example.weatherjourney.util.UiText
import com.example.weatherjourney.weather.data.mapper.toCoordinate
import com.example.weatherjourney.weather.data.mapper.toSavedCity
import com.example.weatherjourney.weather.domain.model.CityUiModel
import com.example.weatherjourney.weather.domain.model.SavedCity
import com.example.weatherjourney.weather.domain.usecase.LocationUseCases
import com.example.weatherjourney.weather.domain.usecase.WeatherUseCases
import com.example.weatherjourney.weather.presentation.search.WeatherSearchEvent.OnCityDelete
import com.example.weatherjourney.weather.presentation.search.WeatherSearchEvent.OnCityLongClick
import com.example.weatherjourney.weather.presentation.search.WeatherSearchEvent.OnCityUpdate
import com.example.weatherjourney.weather.presentation.search.WeatherSearchEvent.OnRefresh
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
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

    private val _locations = locationUseCases.getLocationsStream().map { locations ->
        viewModelScope.launch {
            val channel = Channel<SavedCity>()
            for (location in locations) {
                launch {
                    when (
                        val weather = weatherUseCases.getAllWeather(
                            location.toCoordinate(),
                            location.timeZone
                        )
                    ) {

                        is Result.Success -> {
                            val city = weather.data.toSavedCity(
                                location.cityAddress,
                                location.toCoordinate(),
                                location.timeZone,
                                location.isCurrentLocation
                            )

                            channel.send(city)
                        }

                        is Result.Error -> handleError(weather)
                    }
                }
            }

            var savedCities = emptyList<SavedCity>()

            repeat(locations.size) {
                val city = channel.receive()
                savedCities = if (city.isCurrentLocation) {
                    savedCities.toMutableList().apply { add(0, city) }
                } else {
                    savedCities + city
                }
                uiState = uiState.copy(savedCities = savedCities)
            }
        }
    }

    init {
        Log.d(TAG, "$TAG init")

        viewModelScope.launch {
            if (locationUseCases.validateCurrentCoordinate()) {
                return@launch
            }

            _locations.collect {
                runSuspend(it)
            }
        }
    }

    private val _uiEvent = MutableSharedFlow<UiEvent>()
    val uiEvent = _uiEvent.asSharedFlow()

    private lateinit var tempCityUiModel: CityUiModel

    fun onEvent(event: WeatherSearchEvent) {
        when (event) {
            is OnCityUpdate -> {
                uiState = uiState.copy(cityAddress = event.cityAddress)

                if (event.cityAddress.isNotBlank()) {
                    viewModelScope.launch {
                        when (
                            val suggestionCities =
                                locationUseCases.getSuggestionCities(event.cityAddress)
                        ) {
                            is Result.Success ->
                                uiState =
                                    uiState.copy(suggestionCities = suggestionCities.data)

                            is Result.Error -> handleError(suggestionCities)
                        }
                    }
                }
            }

            is OnRefresh -> viewModelScope.launch {
                runSuspend(_locations.first(), launch { delay(1500) })
            }

            is OnCityLongClick -> viewModelScope.launch {
                if (locationUseCases.validateCurrentLocation(event.city.coordinate)) return@launch

                tempCityUiModel = event.city

                _uiEvent.emit(
                    UiEvent.ShowSnackbar(
                        message = UiText.StringResource(R.string.delete_this_location),
                        actionLabel = R.string.delete
                    )
                )
            }

            is OnCityDelete -> viewModelScope.launch {
                locationUseCases.deleteLocation(tempCityUiModel.coordinate)
            }
        }
    }

    private suspend fun runSuspend(vararg jobs: Job) {
        uiState = uiState.copy(isLoading = true)
        jobs.forEach { it.join() }
        uiState = uiState.copy(isLoading = false)
    }

    private suspend fun handleError(error: Result.Error) {
        val message = error.toString()
        Log.e(TAG, message)
        _uiEvent.emit(UiEvent.ShowSnackbar(UiText.DynamicString(message)))
    }
}
