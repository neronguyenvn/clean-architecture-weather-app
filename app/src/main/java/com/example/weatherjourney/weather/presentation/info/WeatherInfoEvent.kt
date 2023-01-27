package com.example.weatherjourney.weather.presentation.info

import com.example.weatherjourney.weather.domain.model.Coordinate

sealed class WeatherInfoEvent {

    data class OnAppInit(val isLocationPermissionGranted: Boolean) : WeatherInfoEvent()

    data class OnFetchWeatherFromSearch(val city: String, val coordinate: Coordinate) :
        WeatherInfoEvent()

    object OnCacheInfo : WeatherInfoEvent()

    object OnRefresh : WeatherInfoEvent()
}
