package com.example.weatherjourney.core.model.weather

import com.example.weatherjourney.core.common.util.UiText

data class DailyWeather(
    val date: UiText,
    val maxTemp: Float,
    val minTemp: Float,
    val weatherType: WeatherType,
)
