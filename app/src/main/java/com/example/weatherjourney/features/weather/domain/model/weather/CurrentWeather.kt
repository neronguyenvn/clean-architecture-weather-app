package com.example.weatherjourney.features.weather.domain.model.weather

import com.example.weatherjourney.features.weather.domain.model.WeatherType

data class CurrentWeather(
    val date: String,
    val temp: Double,
    val windSpeed: Double,
    val humidity: Double,
    val pressure: Double,
    val weatherType: WeatherType,
)
