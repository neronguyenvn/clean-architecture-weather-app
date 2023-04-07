package com.example.weatherjourney.features.weather.domain.repository

import com.example.weatherjourney.features.weather.data.local.entity.LocationEntity
import com.example.weatherjourney.features.weather.domain.model.Coordinate
import com.example.weatherjourney.features.weather.domain.model.SuggestionCity
import com.example.weatherjourney.util.Result
import kotlinx.coroutines.flow.Flow

interface LocationRepository {

    suspend fun getCurrentCoordinate(): Result<Coordinate>

    suspend fun getSuggestionLocations(cityAddress: String): Result<List<SuggestionCity>>

    suspend fun fetchCurrentLocationIfNeeded(currentCoordinate: Coordinate): Result<Any>

    suspend fun getLocation(coordinate: Coordinate): LocationEntity?

    suspend fun getCurrentLocation(): LocationEntity?

    fun getLocationsStream(): Flow<List<LocationEntity>>

    suspend fun saveLocation(location: LocationEntity)

    suspend fun deleteLocation(location: LocationEntity)

    suspend fun updateLastLocationFromCurrentOne()
}
