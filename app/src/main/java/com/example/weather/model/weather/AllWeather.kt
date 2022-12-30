package com.example.weather.model.weather

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Api Model for All Weather api and also used as Business Model for All Weather DataType
 */
@Serializable
data class AllWeather(
    val current: CurrentWeather,
    val hourly: List<HourlyWeather>,
    val daily: List<DailyWeatherApiModel>
)

/**
 * Api Model for All Weather api and also used as Business Model for Current Weather DataType
 */
@Serializable
data class CurrentWeather(
    @SerialName("dt") val timestamp: Long,
    @SerialName("sunrise") val sunriseTimestamp: Long,
    @SerialName("sunset") val sunsetTimestamp: Long,
    @SerialName("feels_like") val temp: Double,
    @SerialName("weather") val weatherItem: List<WeatherItem>
)

/**
 * Api Model for All Weather api and also used as Business Model for Hourly Weather DataType
 */
@Serializable
data class HourlyWeather(
    val dt: Int,
    @SerialName("feels_like") val temp: Double,
    @SerialName("weather") val weatherItem: List<WeatherItem>
)

/**
 * Api Model for All Weather api
 */
@Serializable
data class DailyWeatherApiModel(
    val dt: Long,
    val temp: Temp,
    @SerialName("weather") val weatherItem: List<WeatherItem>
)

/**
 * Api Model for All Weather api and also used as Business Model when need
 * Weather Description
 */
@Serializable
data class WeatherItem(
    @SerialName("icon") val iconUrl: String,
    @SerialName("main") val weatherDescription: String
)

/**
 * Api Model for All Weather api
 */
@Serializable
data class Temp(
    val max: Double,
    val min: Double
)
