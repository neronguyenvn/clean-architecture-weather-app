package com.example.weather.model.weather

import kotlinx.serialization.Serializable

@Serializable
data class WeatherItem(
    val description: String,
    val icon: String,
    val id: Int,
    val main: String
)