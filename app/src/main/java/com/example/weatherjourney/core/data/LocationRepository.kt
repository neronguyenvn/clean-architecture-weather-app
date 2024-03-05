package com.example.weatherjourney.core.data

import com.example.weatherjourney.core.common.util.Result
import com.example.weatherjourney.core.database.model.LocationEntity
import com.example.weatherjourney.core.model.location.Coordinate
import com.example.weatherjourney.core.model.location.SuggestionCity
import kotlinx.coroutines.flow.Flow

interface LocationRepository {

    suspend fun getCurrentCoordinate(): Result<Coordinate>

    suspend fun getSuggestionCities(cityAddress: String): Result<List<SuggestionCity>>

    suspend fun checkAndUpdateCurrentLocationIfNeeded(currentCoordinate: Coordinate): Result<Boolean>

    suspend fun getLocation(coordinate: Coordinate): LocationEntity?

    suspend fun getCurrentLocation(): LocationEntity?

    fun getLocationsStream(): Flow<List<LocationEntity>>

    suspend fun saveLocation(location: LocationEntity)

    suspend fun deleteLocation(location: LocationEntity)
}
