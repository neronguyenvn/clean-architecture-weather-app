package com.example.weatherjourney.weather.domain.model

data class DailyWeather(
    val date: String,
    val weather: String,
    val imageUrl: String,
    val maxTemp: Int,
    val minTemp: Int
)
