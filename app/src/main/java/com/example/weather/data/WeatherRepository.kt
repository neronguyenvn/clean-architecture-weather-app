package com.example.weather.data

import kotlinx.coroutines.delay

interface WeatherRepository {
    suspend fun getWeather(): String
}

class DefaultWeatherRepository(private val geocodingRepository: GeocodingRepository) : WeatherRepository {
    override suspend fun getWeather(): String {
        delay(2000)
        return geocodingRepository.getLatAndLong().toString()
    }
}