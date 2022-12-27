package com.example.weather.model

import kotlinx.serialization.Serializable

@Serializable
data class DMS(
    val lat: String,
    val lng: String
)