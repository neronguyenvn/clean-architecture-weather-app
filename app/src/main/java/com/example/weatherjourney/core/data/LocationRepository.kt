package com.example.weatherjourney.core.data

import com.example.weatherjourney.core.model.search.Location
import com.example.weatherjourney.core.model.search.LocationWithWeather
import kotlinx.coroutines.flow.Flow

interface LocationRepository {

    fun getLocationsWithWeatherStream(): Flow<List<LocationWithWeather>>

    suspend fun saveLocation(location: Location)

    suspend fun deleteLocation(locationId: Int)

    suspend fun getLocationsByAddress(address: String): Flow<List<Location>>
}
