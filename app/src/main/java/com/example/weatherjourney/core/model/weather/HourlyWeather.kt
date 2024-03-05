package com.example.weatherjourney.core.model.weather

data class HourlyWeather(
    val date: String,
    val temp: Float,
    val windSpeed: Float,
    val weatherType: WeatherType,
)
