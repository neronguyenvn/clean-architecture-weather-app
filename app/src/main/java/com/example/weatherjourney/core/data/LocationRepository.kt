package com.example.weatherjourney.core.data

import com.example.weatherjourney.core.database.model.LocationEntityWithWeather
import com.example.weatherjourney.core.model.search.SuggestionLocation
import kotlinx.coroutines.flow.Flow

interface LocationRepository {

    fun getLocationsWithWeatherStream(): Flow<List<LocationEntityWithWeather>>

    suspend fun saveLocation(location: SuggestionLocation)

    suspend fun deleteLocation(locationId: Int)

    suspend fun getSuggestionLocations(address: String): Result<List<SuggestionLocation>>

    suspend fun makeLocationDisplayed(locationId: Int)
}
