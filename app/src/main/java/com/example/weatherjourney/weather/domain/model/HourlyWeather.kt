package com.example.weatherjourney.weather.domain.model

import com.example.weatherjourney.util.UiText

data class HourlyWeather(
    val date: UiText,
    val temp: Double,
    val windSpeed: Double,
    val weatherType: WeatherType
)
