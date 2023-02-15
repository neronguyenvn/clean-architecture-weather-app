package com.example.weatherjourney.weather.presentation.search

sealed class WeatherSearchEvent {
    data class OnCityUpdate(val cityAddress: String) : WeatherSearchEvent()

    object OnFetchWeatherOfSavedLocations : WeatherSearchEvent()
}
