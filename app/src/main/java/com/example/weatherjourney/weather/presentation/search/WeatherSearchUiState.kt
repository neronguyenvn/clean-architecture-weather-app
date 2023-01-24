package com.example.weatherjourney.weather.presentation.search

import com.example.weatherjourney.weather.domain.model.SuggestionCity

data class WeatherSearchUiState(
    val city: String = "",
    val suggestionCities: List<SuggestionCity> = emptyList()
)
