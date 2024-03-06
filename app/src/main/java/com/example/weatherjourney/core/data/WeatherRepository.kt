package com.example.weatherjourney.core.data

import com.example.weatherjourney.core.database.model.LocationEntity

interface WeatherRepository {

    suspend fun refreshWeatherOfLocation(locationOrDisplayedOne: LocationEntity?)

    suspend fun refreshWeatherOfCurrentLocation()

    suspend fun refreshWeatherOfLocations()
}
