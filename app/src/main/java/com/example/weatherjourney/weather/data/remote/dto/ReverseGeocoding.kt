package com.example.weatherjourney.weather.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class ReverseGeocoding(
    val results: List<ReverseGeocodingResult>
) {
    fun getCityAddress() = results.first().components.run {
        listOf(county, city, region, country).filter { it.isNotBlank() }.joinToString(", ")
    }

    fun getTimeZone() = results.first().annotations.timezone.name
}

@Serializable
data class ReverseGeocodingResult(
    val components: Component,
    val annotations: Annotation
)

@Serializable
data class Component(
    val county: String = "",
    val city: String = "",
    val region: String = "",
    val country: String = ""
)

@Serializable
data class Annotation(
    val timezone: TimeZone
)

@Serializable
data class TimeZone(
    val name: String
)
