package com.example.weather.model

import kotlinx.serialization.Serializable

@Serializable
data class Geocoding(
    val results: List<Result>,
    val status: Status,
    val total_results: Int
)