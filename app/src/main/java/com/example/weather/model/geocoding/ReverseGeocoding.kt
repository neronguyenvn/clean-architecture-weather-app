package com.example.weather.model.geocoding

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
    val city: String
)
