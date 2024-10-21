package com.example.weatherjourney.core.model

data class LocationWithWeather(
    val location: Location,
    val weather: Weather?,
) {
    val id = location.id
}
