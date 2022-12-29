package com.example.weather.model.weather

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class HourlyWeather(
    val dt: Int,
    @SerialName("feels_like") val temp: Double,
    @SerialName("weather") val weatherItem: List<WeatherItem>
)
