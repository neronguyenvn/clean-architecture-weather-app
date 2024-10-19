package com.example.weatherjourney.core.data

import com.example.weatherjourney.core.model.Location
import com.example.weatherjourney.core.model.LocationWithWeather
import kotlinx.coroutines.flow.Flow

interface LocationRepository {

    fun getLocationsWithWeather(): Flow<List<LocationWithWeather>>

    fun getLocationWithWeather(id: Int): Flow<LocationWithWeather>

    suspend fun saveLocation(location: Location)

    suspend fun deleteLocation(id: Int)

    suspend fun getLocationsByAddress(address: String): Flow<List<Location>>
}
