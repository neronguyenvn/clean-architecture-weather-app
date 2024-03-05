package com.example.weatherjourney.core.model.weather

import com.example.weatherjourney.core.common.util.UiText

data class DailyWeather(
    val date: UiText,
    val maxTemp: Double,
    val minTemp: Double,
    val weatherType: WeatherType,
)
