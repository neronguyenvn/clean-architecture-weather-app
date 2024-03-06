package com.example.weatherjourney.core.model.search

import com.example.weatherjourney.core.model.WeatherType

data class SavedLocation(
    val id: Int,
    val temp: Float,
    val weatherType: WeatherType,
    val address: String,
    val countryCode: String,
    val isCurrentLocation: Boolean,
)