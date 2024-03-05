package com.example.weatherjourney.core.network.model

import com.example.weatherjourney.core.database.model.DailyWeatherEntity
import com.example.weatherjourney.core.database.model.HourlyWeatherEntity
import com.example.weatherjourney.core.database.util.FloatListHolder
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
    val temperatures: List<Float>,
    @SerialName("weathercode")
    val weatherCodes: List<Int>,
    @SerialName("pressure_msl")
    val pressures: List<Float>,
    @SerialName("windspeed_10m")
    val windSpeeds: List<Float>,
    @SerialName("relativehumidity_2m")
    val humidities: List<Float>,
)

@Serializable
data class NetworkDailyWeather(
    val time: List<Long>,
    @SerialName("weathercode")
    val weatherCodes: List<Int>,
    @SerialName("temperature_2m_max")
    val maxTemperatures: List<Float>,
    @SerialName("temperature_2m_min")
    val minTemperatures: List<Float>,
)

fun NetworkDailyWeather.asEntity(locationId: Int) = DailyWeatherEntity(
    time = LongListHolder(time),
    weatherCodes = IntListHolder(weatherCodes),
    maxTemperatures = FloatListHolder(maxTemperatures),
    minTemperatures = FloatListHolder(minTemperatures),
    locationId = locationId
)

fun NetworkHourlyWeather.asEntity(locationId: Int) = HourlyWeatherEntity(
    time = LongListHolder(time),
    temperatures = FloatListHolder(temperatures),
    weatherCodes = IntListHolder(weatherCodes),
    pressures = FloatListHolder(pressures),
    windSpeeds = FloatListHolder(windSpeeds),
    humidities = FloatListHolder(humidities),
    locationId = locationId
)