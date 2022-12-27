package com.example.weather.model

import kotlinx.serialization.Serializable

@Serializable
data class Geometry(
    val lat: Double,
    val lng: Double
)