package com.example.weather.model.geocoding

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
    @SerialName("geometry") val coordinate: Coordinate
)

/**
 * Api Model for Forward Geocoding api and also used as Ui Model for Location DataType.
 */
@Serializable
data class Coordinate(
    @SerialName("lat") val latitude: Double,
    @SerialName("lng") val longitude: Double
)
