package com.example.weatherjourney.features.weather.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ReverseGeocoding(
    val results: List<ReverseGeocodingResult>
) {
    fun getCityAddress() = results[0].components.run {
        listOf(suburb, county, city, region).filter { it.isNotBlank() }.joinToString(", ")
    }

    fun getTimeZone() = results[0].annotations.timezone.name

    fun getCountryCode() = results[0].components.countryCode
}

@Serializable
data class ReverseGeocodingResult(
    val components: Component,
    val annotations: Annotation
)

@Serializable
data class Component(
    val suburb: String = "",
    val county: String = "",
    val city: String = "",
    val region: String = "",
    @SerialName("country_code")
    val countryCode: String
)

@Serializable
data class Annotation(
    val timezone: TimeZone
)

@Serializable
data class TimeZone(
    val name: String
)
