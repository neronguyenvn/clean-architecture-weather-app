package com.example.weather.model

import kotlinx.serialization.Serializable

@Serializable
data class Result(
    val geometry: Geometry
)