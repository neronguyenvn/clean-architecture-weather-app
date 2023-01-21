package com.example.weatherjourney.weather.presentation.info

sealed class WeatherInfoEvent {

    object OnSearchClick : WeatherInfoEvent()

    object OnSettingClick : WeatherInfoEvent()

    data class OnStateInit(val isLocationPermissionGranted: Boolean) : WeatherInfoEvent()

    object OnRefresh : WeatherInfoEvent()
}
