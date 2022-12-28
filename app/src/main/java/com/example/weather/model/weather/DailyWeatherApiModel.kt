package com.example.weather.model.weather

import com.example.weather.utils.OPENWEATHER_ICON_BASE_URL
import com.example.weather.utils.toDayNameInWeek
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlin.math.roundToInt

@Serializable
data class DailyWeatherApiModel(
    val dt: Long,
    val temp: Temp,
    @SerialName("weather") val weatherItem: List<WeatherItem>,
)

fun DailyWeatherApiModel.asModel(currentDt: Long): DailyWeather {
    return DailyWeather(
        iconUrl = "$OPENWEATHER_ICON_BASE_URL${weatherItem.first().icon}@2x.png",
        date = dt.toDayNameInWeek(currentDt),
        weather = weatherItem.first().main,
        maxTemp = temp.max.roundToInt(),
        minTemp = temp.min.roundToInt()
    )
}