package com.example.weatherjourney.core.model

data class DailyWeather(
    val date: String,
    val maxTemp: Double,
    val minTemp: Double,
    val weatherType: WeatherType,
)
