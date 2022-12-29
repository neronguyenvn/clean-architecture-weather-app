package com.example.weather.model.weather

import kotlinx.serialization.Serializable

@Serializable
data class WeatherItem(
    val icon: String,
    val main: String
)
