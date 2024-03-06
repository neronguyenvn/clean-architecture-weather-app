package com.example.weatherjourney.core.model.info

import com.example.weatherjourney.core.model.WeatherType

data class HourlyWeather(
    val date: String,
    val temp: Float,
    val windSpeed: Float,
    val weatherType: WeatherType,
)
