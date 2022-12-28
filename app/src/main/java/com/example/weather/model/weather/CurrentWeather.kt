package com.example.weather.model.weather

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CurrentWeather(
    val dt: Long,
    @SerialName("feels_like") val temp: Double,
    @SerialName("weather") val weatherItem: List<WeatherItem>,
)