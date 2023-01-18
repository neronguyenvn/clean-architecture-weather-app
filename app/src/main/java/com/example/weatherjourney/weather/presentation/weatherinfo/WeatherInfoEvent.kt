package com.example.weatherjourney.weather.presentation.weatherinfo

sealed class WeatherInfoEvent {

    object OnSearchClick : WeatherInfoEvent()

    object OnSettingClick : WeatherInfoEvent()

    data class OnLocationPermissionUpdate(val value: Boolean) : WeatherInfoEvent()

    object OnActivityCreate : WeatherInfoEvent()

    object OnRefresh : WeatherInfoEvent()
}
