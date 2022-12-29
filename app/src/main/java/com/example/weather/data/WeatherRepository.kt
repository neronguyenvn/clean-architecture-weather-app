package com.example.weather.data

import com.example.weather.di.IoDispatcher
import com.example.weather.model.geocoding.Location
import com.example.weather.model.weather.Weather
import com.example.weather.network.ApiService
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

interface WeatherRepository {
    suspend fun getCurrentLocation(): Location
    suspend fun getWeather(city: String): Weather
    suspend fun getWeather(location: Location): Weather
    suspend fun getCityByLocation(location: Location): String
}

class DefaultWeatherRepository(
    private val geocodingRepository: GeocodingRepository,
    private val locationRepository: LocationRepository,
    private val apiService: ApiService,
    @IoDispatcher private val dispatcher: CoroutineDispatcher
) : WeatherRepository {

    override suspend fun getCurrentLocation(): Location {
        return locationRepository.getCurrentLocation()
    }

    override suspend fun getCityByLocation(location: Location): String {
        return geocodingRepository.getCity(location)
    }

    override suspend fun getWeather(city: String): Weather = withContext(dispatcher) {
        val location = getLocationByCity(city)
        apiService.getWeather(latitude = location.latitude, longitude = location.longitude)
    }

    override suspend fun getWeather(location: Location): Weather = withContext(dispatcher) {
        apiService.getWeather(latitude = location.latitude, longitude = location.longitude)
    }

    private suspend fun getLocationByCity(city: String): Location {
        return geocodingRepository.getLocation(city)
    }
}
