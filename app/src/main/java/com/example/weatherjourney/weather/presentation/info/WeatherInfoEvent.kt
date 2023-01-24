package com.example.weatherjourney.weather.presentation.info

import com.example.weatherjourney.weather.domain.model.Coordinate

sealed class WeatherInfoEvent {

    object OnSearchClick : WeatherInfoEvent()

    object OnSettingClick : WeatherInfoEvent()

    data class OnAppInit(val isLocationPermissionGranted: Boolean) : WeatherInfoEvent()

    data class OnWeatherFetch(val city: String, val coordinate: Coordinate) : WeatherInfoEvent()

    object OnRefresh : WeatherInfoEvent()
}
