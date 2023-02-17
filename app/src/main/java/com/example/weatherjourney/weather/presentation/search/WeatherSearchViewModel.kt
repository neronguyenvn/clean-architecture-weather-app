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
import com.example.weatherjourney.util.WhileUiSubscribed
import com.example.weatherjourney.weather.data.mapper.toCoordinate
import com.example.weatherjourney.weather.data.mapper.toSavedCity
import com.example.weatherjourney.weather.domain.model.CityUiModel
import com.example.weatherjourney.weather.domain.model.SavedCity
import com.example.weatherjourney.weather.domain.usecase.LocationUseCases
import com.example.weatherjourney.weather.domain.usecase.WeatherUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
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

    private val _locations = locationUseCases.getLocationsStream()
        .map { locations ->
            uiState = uiState.copy(isLoading = true)
            val savedCities = mutableListOf<SavedCity>()

            val job = viewModelScope.launch {
                locations.forEach {
                    launch {
                        when (
                            val weather =
                                weatherUseCases.getAllWeather(it.toCoordinate(), it.timeZone, true)
                        ) {

                            is Result.Success -> {
                                val city = weather.data.toSavedCity(
                                    it.cityAddress,
                                    it.toCoordinate(),
                                    it.timeZone,
                                    it.isCurrentLocation
                                )

                                if (it.isCurrentLocation) {
                                    savedCities.add(0, city)
                                } else {
                                    savedCities.add(city)
                                }
                            }

                            is Result.Error -> handleError(weather)
                        }
                    }
                }
            }

            job.join()
            uiState = uiState.copy(isLoading = false)
            savedCities.toList()
        }.stateIn(
            scope = viewModelScope,
            started = WhileUiSubscribed,
            initialValue = emptyList()
        )

    init {
        Log.d(TAG, "$TAG init")
        uiState = uiState.copy(isLoading = true)

        viewModelScope.launch {
            locationUseCases.validateCurrentCoordinate
            _locations.collect {
                uiState = uiState.copy(savedCities = it)
            }
        }
    }

    private val _uiEvent = MutableSharedFlow<UiEvent>()
    val uiEvent = _uiEvent.asSharedFlow()

    private lateinit var tempCityUiModel: CityUiModel

    fun onEvent(event: WeatherSearchEvent) {
        when (event) {
            is WeatherSearchEvent.OnCityUpdate -> {
                uiState = uiState.copy(cityAddress = event.cityAddress)

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

            is WeatherSearchEvent.OnRefresh -> { /*TODO*/
            }

            is WeatherSearchEvent.OnCityLongClick -> viewModelScope.launch {
                if (locationUseCases.validateCurrentLocation(event.city.coordinate)) return@launch

                tempCityUiModel = event.city

                _uiEvent.emit(
                    UiEvent.ShowSnackbar(
                        message = UiText.StringResource(R.string.delete_this_location),
                        actionLabel = R.string.delete
                    )
                )
            }

            is WeatherSearchEvent.DeleteCity -> viewModelScope.launch {
                locationUseCases.deleteLocation(tempCityUiModel.coordinate)
            }
        }
    }

    private suspend fun handleError(error: Result.Error) {
        val message = error.toString()
        Log.e(TAG, message)
        _uiEvent.emit(UiEvent.ShowSnackbar(UiText.DynamicString(message)))
    }
}
