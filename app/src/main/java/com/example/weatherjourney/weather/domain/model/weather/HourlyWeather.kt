package com.example.weatherjourney.weather.domain.model.weather

import com.example.weatherjourney.util.UiText
import com.example.weatherjourney.weather.domain.model.WeatherType

data class HourlyWeather(
    val date: UiText,
    val temp: Double,
    val windSpeed: Double,
    val weatherType: WeatherType
)
