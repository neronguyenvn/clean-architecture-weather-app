package com.example.weather.model.geocoding

import kotlinx.serialization.Serializable

@Serializable
data class Result(
    val geometry: Geometry
)