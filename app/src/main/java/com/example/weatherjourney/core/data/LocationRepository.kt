package com.example.weatherjourney.core.data

import com.example.weatherjourney.core.database.model.LocationEntityWithWeather
import com.example.weatherjourney.core.model.search.Location
import kotlinx.coroutines.flow.Flow

interface LocationRepository {

    fun getLocationsWithWeatherStream(): Flow<List<LocationEntityWithWeather>>

    suspend fun saveLocation(location: Location)

    suspend fun deleteLocation(locationId: Int)

    suspend fun getLocationsByAddress(address: String): Flow<List<Location>>
}
