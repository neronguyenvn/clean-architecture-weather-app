package com.example.weatherjourney.features.weather.domain.model.weather

import com.example.weatherjourney.features.weather.domain.model.WeatherType

data class HourlyWeather(
    val date: String,
    val temp: Double,
    val windSpeed: Double,
    val weatherType: WeatherType
)
