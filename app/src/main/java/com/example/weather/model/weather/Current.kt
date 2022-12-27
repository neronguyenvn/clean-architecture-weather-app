package com.example.weather.model.weather

import kotlinx.serialization.Serializable

@Serializable
data class Current(
    val dt: Long,
    val temp: Double,
    val weather: List<WeatherItem>,
)