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
    val country: String = "",
    @SerialName("country_code") val countryCode: String,
    val latitude: Double,
    val longitude: Double,
    val timezone: String
) {
    fun getCityAddress() = listOf(name, admin1, country).filter { it.isNotBlank() }
        .joinToString(", ")
}
