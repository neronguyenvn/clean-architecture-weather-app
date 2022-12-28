package com.example.weather.model.weather

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CurrentWeather(
    @SerialName("dt") val timestamp: Long,
    @SerialName("sunrise") val sunriseTimestamp: Long,
    @SerialName("sunset") val sunsetTimestamp: Long,
    @SerialName("feels_like") val temp: Double,
    @SerialName("weather") val weatherItem: List<WeatherItem>,
)