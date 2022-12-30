package com.example.weather.model.weather

import com.example.weather.utils.OPENWEATHER_ICON_BASE_URL
import com.example.weather.utils.toDayNameInWeek
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlin.math.roundToInt

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

// Convert Daily Weather Api Model into Business Model one
fun DailyWeatherApiModel.asModel(currentDt: Long): DailyWeather {
    return DailyWeather(
        iconUrl = "$OPENWEATHER_ICON_BASE_URL${weatherItem.first().iconUrl}@2x.png",
        date = dt.toDayNameInWeek(currentDt),
        weather = weatherItem.first().weatherDescription,
        maxTemp = temp.max.roundToInt(),
        minTemp = temp.min.roundToInt()
    )
}
