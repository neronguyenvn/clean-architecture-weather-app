package com.example.weatherjourney.core.model.info

import com.example.weatherjourney.core.common.util.UiText
import com.example.weatherjourney.core.model.WeatherType

data class DailyWeather(
    val date: UiText,
    val maxTemp: Float,
    val minTemp: Float,
    val weatherType: WeatherType,
)
