package com.example.weather.model.geocoding

import kotlinx.serialization.Serializable

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
