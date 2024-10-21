package com.example.weatherjourney.core.data

import com.example.weatherjourney.core.model.Coordinate

interface WeatherRepository {

    suspend fun refreshWeatherOfLocations()

    suspend fun refreshWeatherOfLocation(locationId: Int)
}
