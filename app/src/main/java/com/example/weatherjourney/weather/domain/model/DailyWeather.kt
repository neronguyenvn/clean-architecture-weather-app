package com.example.weatherjourney.weather.domain.model

import com.example.weatherjourney.util.UiText

data class DailyWeather(
    val date: UiText,
    val maxTemp: Double,
    val minTemp: Double,
    val weatherType: WeatherType
)
