package com.example.weatherjourney.weather.domain.model

data class CurrentWeather(
    val date: String,
    val temp: Int,
    val weather: String,
    val imageUrl: String,
    val realFeelTemp: Int,
    val humidity: Int,
    val rainChance: Int,
    val pressure: Int,
    val visibility: Int,
    val uvIndex: Int
)
