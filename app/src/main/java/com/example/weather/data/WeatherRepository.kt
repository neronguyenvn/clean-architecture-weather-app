package com.example.weather.data

interface WeatherRepository {
    suspend fun getWeather(city: String): String
}

class DefaultWeatherRepository(private val geocodingRepository: GeocodingRepository) :
    WeatherRepository {
    override suspend fun getWeather(city: String): String {
        return geocodingRepository.getLatAndLong(city).toString()
    }
}