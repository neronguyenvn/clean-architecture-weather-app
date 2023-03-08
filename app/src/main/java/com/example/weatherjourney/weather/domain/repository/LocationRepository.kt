package com.example.weatherjourney.weather.domain.repository

import com.example.weatherjourney.util.Result
import com.example.weatherjourney.weather.data.local.entity.LocationEntity
import com.example.weatherjourney.weather.domain.model.Coordinate
import com.example.weatherjourney.weather.domain.model.SuggestionCity
import kotlinx.coroutines.flow.Flow

interface LocationRepository {

    suspend fun getCurrentCoordinate(): Result<Coordinate>

    suspend fun getSuggestionLocations(cityAddress: String): Result<List<SuggestionCity>>

    suspend fun fetchLocation(coordinate: Coordinate, shouldUpdateLastLocation: Boolean): Result<Any>

    suspend fun getLocation(coordinate: Coordinate): LocationEntity?

    suspend fun getCurrentLocation(): LocationEntity?

    fun getLocationsStream(): Flow<List<LocationEntity>>

    suspend fun saveLocation(location: LocationEntity)

    suspend fun deleteLocation(location: LocationEntity)
}
