package com.example.weather.model.weather

/**
 * Ui Model for Daily Weather DataType
 */
data class DailyWeather(
    val iconUrl: String,
    val date: String,
    val weather: String,
    val maxTemp: Int,
    val minTemp: Int
)
