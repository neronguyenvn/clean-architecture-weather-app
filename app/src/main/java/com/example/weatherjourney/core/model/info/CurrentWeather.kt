package com.example.weatherjourney.core.model.info

import com.example.weatherjourney.core.model.WeatherType

data class CurrentWeather(
    val date: String,
    val temp: Float,
    val windSpeed: Float,
    val humidity: Float,
    val pressure: Float,
    val weatherType: WeatherType,
)
