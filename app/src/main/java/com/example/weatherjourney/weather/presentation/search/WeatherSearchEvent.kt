package com.example.weatherjourney.weather.presentation.search

import com.example.weatherjourney.weather.domain.model.CityUiModel

sealed class WeatherSearchEvent {

    data class OnCityUpdate(val cityAddress: String) : WeatherSearchEvent()

    object OnRefresh : WeatherSearchEvent()

    data class OnCityLongClick(val city: CityUiModel) : WeatherSearchEvent()

    object DeleteCity : WeatherSearchEvent()
}
