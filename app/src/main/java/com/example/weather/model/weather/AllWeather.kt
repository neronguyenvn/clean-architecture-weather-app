package com.example.weather.model.weather

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Api Model for All Weather api and also used as Ui Model for All Weather DataType.
 */
@Serializable
data class AllWeather(
    val current: CurrentWeatherApiModel,
    val hourly: List<HourlyWeather>,
    val daily: List<DailyWeatherApiModel>,
    @SerialName("timezone_offset") val timezoneOffset: Int
)

/**
 * Api Model for All Weather api.
 */
@Serializable
data class CurrentWeatherApiModel(
    @SerialName("dt") val timestamp: Long,
    @SerialName("sunrise") val sunriseTimestamp: Long? = null,
    @SerialName("sunset") val sunsetTimestamp: Long? = null,
    @SerialName("feels_like") val temp: Double,
    @SerialName("weather") val weatherItem: List<WeatherItem>
) {

    constructor(timestamp: Long, temp: Double) : this(
        timestamp = timestamp,
        temp = temp,
        weatherItem = listOf(WeatherItem("", ""))
    )
}

/**
 * Api Model for All Weather api and also used as Ui Model for Hourly Weather DataType.
 */
@Serializable
data class HourlyWeather(
    @SerialName("dt") val timestamp: Long,
    @SerialName("feels_like") val temp: Double,
    @SerialName("weather") val weatherItem: List<WeatherItem>
) {
    constructor(timestamp: Long, temp: Double) : this(
        timestamp = timestamp,
        temp = temp,
        weatherItem = listOf(WeatherItem("", ""))
    )
}

/**
 * Api Model for All Weather api.
 */
@Serializable
data class DailyWeatherApiModel(
    @SerialName("dt") val timestamp: Long,
    val temp: Temp,
    @SerialName("weather") val weatherItem: List<WeatherItem>
) {
    constructor(timestamp: Long, temp: Temp) : this(
        timestamp = timestamp,
        temp = temp,
        weatherItem = listOf(WeatherItem("", ""))
    )
}

/**
 * Api Model for All Weather api and also used as Ui Model when need
 * Weather Description.
 */
@Serializable
data class WeatherItem(
    @SerialName("icon") val iconUrl: String,
    @SerialName("main") val weatherDescription: String
)

/**
 * Api Model for All Weather api.
 */
@Serializable
data class Temp(
    val max: Double,
    val min: Double
)
