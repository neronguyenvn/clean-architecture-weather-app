package com.example.weatherjourney.weather.domain.model.weather

import com.example.weatherjourney.util.UiText
import com.example.weatherjourney.weather.domain.model.WeatherType

data class DailyWeather(
    val date: UiText,
    val maxTemp: Double,
    val minTemp: Double,
    val weatherType: WeatherType
)
