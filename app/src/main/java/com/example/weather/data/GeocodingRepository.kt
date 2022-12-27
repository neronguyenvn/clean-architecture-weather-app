package com.example.weather.data

import com.example.weather.model.Geometry
import com.example.weather.network.ApiService

interface GeocodingRepository {
    suspend fun getLatAndLong(city: String): Geometry
}

class DefaultGeocodingRepository(
    private val apiService: ApiService
) : GeocodingRepository {
    override suspend fun getLatAndLong(city: String): Geometry {
        return apiService.getGeocoding(city = city).results.first().geometry
    }
}
