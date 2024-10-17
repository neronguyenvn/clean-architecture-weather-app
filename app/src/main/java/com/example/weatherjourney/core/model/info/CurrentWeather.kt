package com.example.weatherjourney.core.model.info

import com.example.weatherjourney.core.model.WeatherType

data class CurrentWeather(
    val date: String,
    val temp: Double,
    val windSpeed: Double,
    val humidity: Double,
    val pressure: Double,
    val weatherType: WeatherType,
)
