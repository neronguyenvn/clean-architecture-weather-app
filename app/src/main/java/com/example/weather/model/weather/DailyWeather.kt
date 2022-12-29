package com.example.weather.model.weather

data class DailyWeather(
    val iconUrl: String,
    val date: String,
    val weather: String,
    val maxTemp: Int,
    val minTemp: Int
)
