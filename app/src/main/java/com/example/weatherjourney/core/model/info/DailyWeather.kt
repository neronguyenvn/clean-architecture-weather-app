package com.example.weatherjourney.core.model.info

import com.example.weatherjourney.core.model.WeatherType

data class DailyWeather(
    val date: String,
    val maxTemp: Double,
    val minTemp: Double,
    val weatherType: WeatherType,
)
