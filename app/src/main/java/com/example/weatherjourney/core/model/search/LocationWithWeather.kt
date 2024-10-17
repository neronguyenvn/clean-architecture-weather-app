package com.example.weatherjourney.core.model.search

import com.example.weatherjourney.core.model.info.Weather

data class LocationWithWeather(
    val location: Location,
    val weather: Weather?,
)
