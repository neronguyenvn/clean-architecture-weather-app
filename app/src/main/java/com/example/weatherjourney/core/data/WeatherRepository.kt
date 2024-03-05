package com.example.weatherjourney.core.data

interface WeatherRepository {

    suspend fun refreshWeatherOfDisplayedLocation()

    suspend fun refreshWeatherOfCurrentLocation()
}
