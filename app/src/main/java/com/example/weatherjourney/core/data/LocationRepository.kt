package com.example.weatherjourney.core.data

import com.example.weatherjourney.core.common.util.Result
import com.example.weatherjourney.core.database.model.LocationEntity
import com.example.weatherjourney.core.database.model.LocationEntityWithWeather
import com.example.weatherjourney.core.model.search.SuggestionLocation
import kotlinx.coroutines.flow.Flow

interface LocationRepository {

    fun getDisplayedLocationStream(): Flow<LocationEntity?>

    fun getDisplayedLocationWithWeatherStream(): Flow<LocationEntityWithWeather?>

    fun getAllLocationWithWeatherStream(): Flow<List<LocationEntityWithWeather>>

    suspend fun saveLocation(suggestionLocation: SuggestionLocation)

    suspend fun deleteLocation(locationId: Int)

    suspend fun getSuggestionLocations(address: String): Result<List<SuggestionLocation>>

    suspend fun makeLocationDisplayed(locationId: Int)
}
