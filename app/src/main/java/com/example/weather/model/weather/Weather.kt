package com.example.weather.model.weather

import kotlinx.serialization.Serializable

@Serializable
data class Weather(
    val current: Current,
    val daily: List<Daily>,
    val hourly: List<Hourly>,
)