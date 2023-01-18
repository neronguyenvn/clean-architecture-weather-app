package com.example.weatherjourney.weather.domain.model

data class HourlyWeather(
    val date: String,
    val imageUrl: String,
    val temp: Int,
    val windSpeed: Int
)
