package com.example.weather.data

import com.example.weather.di.IoDispatcher
import com.example.weather.model.geocoding.Location
import com.example.weather.network.ApiService
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

/**
 * Interface for Repository of Geocoding DataType
 */
interface GeocodingRepository {
    /**
     * Get Location by call Api to convert City name into it
     */
    suspend fun getLocation(city: String): Location

    /**
     * Get City name by call Api to convert Location into it
     */
    suspend fun getCity(location: Location): String
}

/**
 * Implementation of Interface for Repository of Geocoding DataType
 */
class DefaultGeocodingRepository(
    private val apiService: ApiService,
    @IoDispatcher private val dispatcher: CoroutineDispatcher
) : GeocodingRepository {

    override suspend fun getLocation(city: String): Location = withContext(dispatcher) {
        apiService.getGeocoding(city = city).results.first().location
    }

    override suspend fun getCity(location: Location): String = withContext(dispatcher) {
        apiService.getCity("${location.latitude}+${location.longitude}")
            .results.first().components.city
    }
}
