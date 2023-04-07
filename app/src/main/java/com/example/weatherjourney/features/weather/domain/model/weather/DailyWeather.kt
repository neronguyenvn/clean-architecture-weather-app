package com.example.weatherjourney.features.weather.domain.model.weather

import com.example.weatherjourney.features.weather.domain.model.WeatherType
import com.example.weatherjourney.util.UiText

data class DailyWeather(
    val date: UiText,
    val maxTemp: Double,
    val minTemp: Double,
    val weatherType: WeatherType
)
