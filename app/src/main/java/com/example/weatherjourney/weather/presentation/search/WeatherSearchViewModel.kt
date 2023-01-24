package com.example.weatherjourney.weather.presentation.search

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherjourney.util.Result
import com.example.weatherjourney.util.UiEvent
import com.example.weatherjourney.util.UiText
import com.example.weatherjourney.weather.domain.repository.LocationRepository
import com.example.weatherjourney.weather.presentation.search.WeatherSearchEvent.OnCityUpdate
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WeatherSearchViewModel @Inject constructor(
    private val repository: LocationRepository
) : ViewModel() {

    var uiState by mutableStateOf(WeatherSearchUiState())
        private set

    private val _uiEvent = Channel<UiEvent>()
    val uiEvent = _uiEvent.receiveAsFlow()

    fun onEvent(event: WeatherSearchEvent) {
        when (event) {
            is OnCityUpdate -> {
                uiState = uiState.copy(city = event.city)
                fetchSuggestionCities(event.city)
            }
        }
    }

    private fun fetchSuggestionCities(city: String) {
        if (city.isNotBlank()) {
            viewModelScope.launch {
                when (val suggestionCities = repository.fetchSuggestionLocations(city)) {
                    is Result.Success -> {
                        uiState =
                            uiState.copy(suggestionCities = suggestionCities.data.results.map { it.getFormattedLocationString() })
                    }

                    is Result.Error -> {
                        val message = suggestionCities.toString()
                        _uiEvent.send(UiEvent.ShowSnackbar(UiText.DynamicString(message)))
                    }
                }
            }
        }
    }
}
