package com.example.weatherjourney.core.model.info

import com.example.weatherjourney.core.model.WeatherType

data class HourlyWeather(
    val date: String,
    val temp: Double,
    val windSpeed: Double,
    val weatherType: WeatherType,
)
