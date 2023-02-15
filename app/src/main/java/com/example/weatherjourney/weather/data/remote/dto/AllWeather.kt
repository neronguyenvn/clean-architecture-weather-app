package com.example.weatherjourney.weather.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AllWeather(
    val hourly: HourlyWeatherDto,
    val daily: DailyWeatherDto
)

@Serializable
data class HourlyWeatherDto(
    val time: List<Long>,
    @SerialName("temperature_2m")
    val temperatures: List<Double>,
    @SerialName("weathercode")
    val weatherCodes: List<Int>,
    @SerialName("surface_pressure")
    val pressures: List<Double>,
    @SerialName("windspeed_10m")
    val windSpeeds: List<Double>,
    @SerialName("relativehumidity_2m")
    val humidities: List<Double>
)

@Serializable
data class DailyWeatherDto(
    val time: List<Long>,
    @SerialName("weathercode")
    val weatherCodes: List<Int>,
    @SerialName("temperature_2m_max")
    val maxTemperatures: List<Double>,
    @SerialName("temperature_2m_min")
    val minTemperatures: List<Double>
)
