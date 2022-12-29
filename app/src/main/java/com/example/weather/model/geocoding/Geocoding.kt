package com.example.weather.model.geocoding

import kotlinx.serialization.Serializable

@Serializable
data class Geocoding(
    val results: List<Result>
)
