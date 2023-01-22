package com.example.weatherjourney.weather.presentation.search

data class WeatherSearchUiState(
    val city: String = "",
    val isSearching: Boolean = false,
    val suggestionCities: List<String> = emptyList()
)
