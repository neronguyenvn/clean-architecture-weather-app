package com.example.weatherjourney.weather.presentation.setting

sealed class WeatherSettingEvent {

    data class OnTemperatureLabelUpdate(val label: String) : WeatherSettingEvent()

    data class OnWindSpeedLabelUpdate(val label: String) : WeatherSettingEvent()
}
