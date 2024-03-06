package com.example.weatherjourney.core.network.model

import com.example.weatherjourney.core.database.model.LocationEntity
import com.example.weatherjourney.core.model.Coordinate
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ReverseGeocoding(
    val results: List<ReverseGeocodingResult>,
) {
    fun getAddress() = results[0].components.run {
        listOf(suburb, county, city, region).filter { it.isNotBlank() }.joinToString(", ")
    }

    fun getTimeZone() = results[0].annotations.timezone.name

    fun getCountryCode() = results[0].components.countryCode
}

@Serializable
data class ReverseGeocodingResult(
    val components: Component,
    val annotations: Annotation,
)

@Serializable
data class Component(
    val suburb: String = "",
    val county: String = "",
    val city: String = "",
    val region: String = "",
    @SerialName("country_code")
    val countryCode: String,
)

@Serializable
data class Annotation(
    val timezone: TimeZone,
)

@Serializable
data class TimeZone(
    val name: String,
)

fun ReverseGeocoding.asEntity(coordinate: Coordinate) = LocationEntity(
    address = getAddress(),
    countryCode = getCountryCode(),
    timeZone = getTimeZone(),
    latitude = coordinate.latitude,
    longitude = coordinate.longitude,
    isDisplayed = true
)