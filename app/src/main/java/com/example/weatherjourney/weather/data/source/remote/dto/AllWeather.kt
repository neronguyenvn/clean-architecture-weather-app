package com.example.weatherjourney.weather.data.source.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AllWeather(
    val current: CurrentWeatherDto,
    val hourly: List<HourlyWeatherDto>,
    val daily: List<DailyWeatherDto>,
    @SerialName("timezone_offset") val timezoneOffset: Int
)

@Serializable
data class CurrentWeatherDto(
    @SerialName("dt") val timestamp: Long,
    @SerialName("feels_like") val realFeelTemp: Double,
    @SerialName("weather") val weatherItem: List<WeatherItem>,
    val pressure: Int,
    val temp: Double,
    val humidity: Int,
    val uvi: Double,
    val visibility: Int
)

@Serializable
data class HourlyWeatherDto(
    @SerialName("dt") val timestamp: Long,
    @SerialName("feels_like") val temp: Double,
    @SerialName("weather") val weatherItem: List<WeatherItem>,
    @SerialName("pop") val precipitationChance: Double,
    @SerialName("wind_speed") val windSpeed: Double
)

@Serializable
data class DailyWeatherDto(
    @SerialName("dt") val timestamp: Long,
    val temp: Temp,
    @SerialName("weather") val weatherItem: List<WeatherItem>
)

@Serializable
data class WeatherItem(
    @SerialName("icon") val imageUri: String,
    val description: String
)

@Serializable
data class Temp(
    val max: Double,
    val min: Double
)
