package com.example.weatherjourney.weather.data.source.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class ForwardGeocoding(
    val results: List<ForwardGeocodingResult>
)

@Serializable
data class ForwardGeocodingResult(
    val name: String,
    val admin1: String = "",
    val admin2: String = "",
    val country: String
) {
    fun getFormattedLocationString() =
        listOf(name, admin2, admin1, country).filter { it.isNotBlank() }.joinToString(", ")
}
