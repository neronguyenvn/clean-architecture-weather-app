package com.example.weather.data

import com.example.weather.model.geocoding.Location
import com.example.weather.network.ApiService

interface GeocodingRepository {
    suspend fun getLocation(city: String): Location
}

class DefaultGeocodingRepository(
    private val apiService: ApiService
) : GeocodingRepository {
    override suspend fun getLocation(city: String): Location {
        return apiService.getGeocoding(city = city).results.first().location
    }
}
