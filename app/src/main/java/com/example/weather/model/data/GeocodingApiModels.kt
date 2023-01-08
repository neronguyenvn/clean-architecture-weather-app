package com.example.weather.model.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Api Model for Forward Geocoding api meaning convert CityName into Location.
 */
@Serializable
data class ForwardGeocoding(
    val results: List<ForwardGeocodingResult>
)

/**
 * Api Model for Forward Geocoding api.
 */
@Serializable
data class ForwardGeocodingResult(
    @SerialName("geometry") val coordinate: Coordinate,
    @SerialName("formatted") val address: String
)

/**
 * Api Model for Forward Geocoding api and also used as Ui Model for Location DataType.
 */
@Serializable
data class Coordinate(
    @SerialName("lat") val latitude: Double,
    @SerialName("lng") val longitude: Double
)

/**
 * Api Model for Reverse Geocoding api meaning convert Location into CityName.
 */
@Serializable
data class ReverseGeocoding(
    val results: List<ReverseGeocodingResult>
)

/**
 * Api Model for Reverse Geocoding api.
 */
@Serializable
data class ReverseGeocodingResult(
    val components: Components
)

/**
 * Api Model for Reverse Geocoding api.
 */
@Serializable
data class Components(
    val city: String
)
