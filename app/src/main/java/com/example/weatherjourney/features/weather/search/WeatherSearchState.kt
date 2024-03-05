package com.example.weatherjourney.features.weather.search

import com.example.weatherjourney.core.common.util.UserMessage
import com.example.weatherjourney.core.model.location.SavedCity
import com.example.weatherjourney.core.model.location.SuggestionCity

sealed interface WeatherSearchState {
    val input: String
    val isLoading: Boolean
    val userMessage: UserMessage?

    data class ShowSaveCities(
        val savedCities: List<SavedCity>,
        override val input: String,
        override val isLoading: Boolean,
        override val userMessage: UserMessage?,
    ) : WeatherSearchState

    data class ShowSuggestionCities(
        val suggestionCities: List<SuggestionCity>,
        override val input: String,
        override val isLoading: Boolean,
        override val userMessage: UserMessage?,
    ) : WeatherSearchState
}

data class WeatherSearchViewModelState(
    val input: String = "",
    val isLoading: Boolean = false,
    val userMessage: UserMessage? = null,
    val savedCities: List<SavedCity> = emptyList(),
    val suggestionCities: List<SuggestionCity> = emptyList(),
) {
    fun toUiState(): WeatherSearchState =
        if (input.isBlank()) {
            WeatherSearchState.ShowSaveCities(
                input = input,
                isLoading = isLoading,
                userMessage = userMessage,
                savedCities = savedCities,
            )
        } else {
            WeatherSearchState.ShowSuggestionCities(
                input = input,
                isLoading = isLoading,
                userMessage = userMessage,
                suggestionCities = suggestionCities,
            )
        }
}
