package com.example.weatherjourney.core.model

data class HourlyWeather(
    val date: String,
    val temp: Double,
    val windSpeed: Double,
    val weatherType: WeatherType,
)
