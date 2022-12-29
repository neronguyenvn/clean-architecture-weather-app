package com.example.weather.model.geocoding

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ForwardGeocoding(
    val results: List<ForwardGeocodingResult>
)

@Serializable
data class ForwardGeocodingResult(
    @SerialName("geometry") val location: Location
)

@Serializable
data class Location(
    @SerialName("lat") val latitude: Double,
    @SerialName("lng") val longitude: Double
)
