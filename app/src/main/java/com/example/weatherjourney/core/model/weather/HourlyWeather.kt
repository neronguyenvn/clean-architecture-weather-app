package com.example.weatherjourney.core.model.weather

data class HourlyWeather(
    val date: String,
    val temp: Double,
    val windSpeed: Double,
    val weatherType: WeatherType,
)
