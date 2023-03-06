package com.example.weatherjourney.weather.presentation.search

import com.example.weatherjourney.util.UserMessage
import com.example.weatherjourney.weather.domain.model.SavedCity
import com.example.weatherjourney.weather.domain.model.SuggestionCity

sealed interface WeatherSearchUiState {
    val input: String
    val isLoading: Boolean
    val userMessage: UserMessage?

    data class ShowSaveCities(
        val savedCities: List<SavedCity>,
        override val input: String,
        override val isLoading: Boolean,
        override val userMessage: UserMessage?
    ) : WeatherSearchUiState

    data class ShowSuggestionCities(
        val suggestionCities: List<SuggestionCity>,
        override val input: String,
        override val isLoading: Boolean,
        override val userMessage: UserMessage?
    ) : WeatherSearchUiState
}
