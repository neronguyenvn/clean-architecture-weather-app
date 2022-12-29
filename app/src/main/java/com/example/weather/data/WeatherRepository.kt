package com.example.weather.data

import com.example.weather.model.geocoding.Location
import com.example.weather.model.weather.Weather
import com.example.weather.network.ApiService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

interface WeatherRepository {
    suspend fun getWeather(city: String): Weather
    val currentCityWeather: Flow<Weather>
}

class DefaultWeatherRepository(
    private val geocodingRepository: GeocodingRepository,
    locationRepository: LocationRepository,
    private val apiService: ApiService
) : WeatherRepository {
    override suspend fun getWeather(city: String): Weather {
        val location = getLocation(city)
        return apiService.getWeather(lat = location.lat, lon = location.lng)
    }

    override val currentCityWeather: Flow<Weather> =
        locationRepository.location.map { location ->
            apiService.getWeather(lat = location.lat, lon = location.lng)
        }

    private suspend fun getLocation(city: String): Location {
        return geocodingRepository.getLocation(city)
    }
}