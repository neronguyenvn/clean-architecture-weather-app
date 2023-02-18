package com.example.weatherjourney.weather.presentation.setting

sealed class WeatherSettingEvent {
    data class OnTemperatureUnitUpdate(val unitLabel: String) : WeatherSettingEvent()
}
