package com.example.weather.data

import com.example.weather.model.geocoding.Geometry
import com.example.weather.network.ApiService

interface GeocodingRepository {
    suspend fun getGeometry(city: String): Geometry
}

class DefaultGeocodingRepository(
    private val apiService: ApiService
) : GeocodingRepository {
    override suspend fun getGeometry(city: String): Geometry {
        return apiService.getGeocoding(city = city).results.first().geometry
    }
}
