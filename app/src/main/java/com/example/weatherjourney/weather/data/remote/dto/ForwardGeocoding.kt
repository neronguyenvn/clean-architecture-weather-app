package com.example.weatherjourney.weather.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ForwardGeocoding(
    val results: List<ForwardGeocodingResult> = emptyList()
)

@Serializable
data class ForwardGeocodingResult(
    val name: String,
    val admin1: String = "",
    val admin2: String = "",
    @SerialName("country_code") val countryCode: String,
    val latitude: Double,
    val longitude: Double,
    val timezone: String = ""
) {
    fun getCityAddress() = listOf(name, admin2, admin1).filter { it.isNotBlank() }
        .joinToString(", ")
}
