package com.example.weather.model.weather

import kotlinx.serialization.Serializable

@Serializable
data class Temp(
    val max: Double,
    val min: Double
)
