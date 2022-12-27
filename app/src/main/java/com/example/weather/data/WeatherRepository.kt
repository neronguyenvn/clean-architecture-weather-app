package com.example.weather.data

import com.example.weather.model.weather.Weather
import com.example.weather.network.ApiService

interface WeatherRepository {
    suspend fun getWeather(city: String): Weather
}

class DefaultWeatherRepository(
    private val geocodingRepository: GeocodingRepository,
    private val apiService: ApiService
) :
    WeatherRepository {
    override suspend fun getWeather(city: String): Weather {
        val geometry = geocodingRepository.getGeometry(city)
        return apiService.getWeather(lat = geometry.lat, lon = geometry.lng)
    }
}