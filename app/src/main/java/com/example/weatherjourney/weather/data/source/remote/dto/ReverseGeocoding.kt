package com.example.weatherjourney.weather.data.source.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class ReverseGeocoding(
    val results: List<ReverseGeocodingResult>
)

@Serializable
data class ReverseGeocodingResult(
    val components: Components
)

@Serializable
data class Components(
    val county: String = "",
    val city: String = "",
    val region: String = "",
    val country: String = ""
) {
    fun getFormattedLocationString() =
        listOf(county, city, region, country).filter { it.isNotBlank() }.joinToString(", ")
}
