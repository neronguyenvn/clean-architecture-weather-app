package com.example.weatherjourney.weather.presentation.search

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherjourney.weather.presentation.search.WeatherSearchEvent.OnCityUpdate
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WeatherSearchViewModel @Inject constructor() : ViewModel() {

    var uiState by mutableStateOf(WeatherSearchUiState())
        private set

    fun onEvent(event: WeatherSearchEvent) {
        when (event) {
            is OnCityUpdate -> {
                uiState = uiState.copy(city = event.city)
                fetchSuggestionCities(event.city)
            }
        }
    }

    private fun fetchSuggestionCities(city: String) {
        viewModelScope.launch {
            uiState = uiState.copy(isSearching = true)
            delay(500)
            uiState = uiState.copy(
                suggestionCities = if (city.isNotBlank()) {
                    listOf(
                        "\uD83C\uDDFA\uD83C\uDDF8  United States",
                        "\uD83C\uDDFA\uD83C\uDDF8  United States",
                        "\uD83C\uDDFA\uD83C\uDDF8  United States"
                    )
                } else {
                    emptyList()
                }
            )
            uiState = uiState.copy(isSearching = false)
        }
    }
}
