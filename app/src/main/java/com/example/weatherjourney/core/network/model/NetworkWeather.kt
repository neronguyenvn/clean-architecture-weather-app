package com.example.weatherjourney.core.network.model

import com.example.weatherjourney.core.database.model.DailyWeatherEntity
import com.example.weatherjourney.core.database.model.HourlyWeatherEntity
import com.example.weatherjourney.core.database.util.DoubleListHolder
import com.example.weatherjourney.core.database.util.IntListHolder
import com.example.weatherjourney.core.database.util.LongListHolder
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class NetworkWeather(
    val hourly: NetworkHourlyWeather,
    val daily: NetworkDailyWeather,
)

@Serializable
data class NetworkHourlyWeather(
    val time: List<Long>,
    @SerialName("temperature_2m")
    val temperatures: List<Double>,
    @SerialName("weathercode")
    val weatherCodes: List<Int>,
    @SerialName("pressure_msl")
    val pressures: List<Double>,
    @SerialName("windspeed_10m")
    val windSpeeds: List<Double>,
    @SerialName("relativehumidity_2m")
    val humanities: List<Double>,
)

@Serializable
data class NetworkDailyWeather(
    val time: List<Long>,
    @SerialName("weathercode")
    val weatherCodes: List<Int>,
    @SerialName("temperature_2m_max")
    val maxTemperatures: List<Double>,
    @SerialName("temperature_2m_min")
    val minTemperatures: List<Double>,
)

fun NetworkDailyWeather.asEntity(locationId: Int) = DailyWeatherEntity(
    time = LongListHolder(time),
    weatherCodes = IntListHolder(weatherCodes),
    maxTemperatures = DoubleListHolder(maxTemperatures),
    minTemperatures = DoubleListHolder(minTemperatures),
    locationId = locationId
)

fun NetworkHourlyWeather.asEntity(locationId: Int) = HourlyWeatherEntity(
    time = LongListHolder(time),
    temperatures = DoubleListHolder(temperatures),
    weatherCodes = IntListHolder(weatherCodes),
    pressures = DoubleListHolder(pressures),
    windSpeeds = DoubleListHolder(windSpeeds),
    humidities = DoubleListHolder(humanities),
    locationId = locationId
)