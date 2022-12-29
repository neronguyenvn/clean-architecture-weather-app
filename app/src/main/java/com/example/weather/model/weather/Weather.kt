package com.example.weather.model.weather

import kotlinx.serialization.Serializable

@Serializable
data class Weather(
    val current: CurrentWeather,
    val hourly: List<HourlyWeather>,
    val daily: List<DailyWeatherApiModel>
)
