package com.example.weatherjourney.weather.domain.model

data class CurrentWeather(
    val date: String,
    val temp: Double,
    val windSpeed: Double,
    val humidity: Double,
    val pressure: Double,
    val weatherType: WeatherType
)
