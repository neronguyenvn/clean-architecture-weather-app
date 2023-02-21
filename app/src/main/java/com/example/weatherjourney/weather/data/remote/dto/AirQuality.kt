package com.example.weatherjourney.weather.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AirQuality(
    val hourly: HourlyAirQuality
)

@Serializable
data class HourlyAirQuality(
    val time: List<Long>,
    @SerialName("uv_index")
    val uvIndexList: List<Double?>,
    @SerialName("european_aqi")
    val europeanAqiList: List<Int?>
)
