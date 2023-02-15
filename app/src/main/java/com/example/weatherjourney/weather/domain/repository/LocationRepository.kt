package com.example.weatherjourney.weather.domain.repository

import com.example.weatherjourney.util.Result
import com.example.weatherjourney.weather.data.local.entity.LocationEntity
import com.example.weatherjourney.weather.domain.model.Coordinate
import com.example.weatherjourney.weather.domain.model.SuggestionCity

interface LocationRepository {

    suspend fun getCurrentCoordinate(): Result<Coordinate>

    suspend fun getSuggestionLocations(cityAddress: String): Result<List<SuggestionCity>>

    suspend fun fetchCity(coordinate: Coordinate): Result<String>

    suspend fun getLocation(coordinate: Coordinate): LocationEntity?

    suspend fun saveLocation(location: LocationEntity)

    suspend fun getLocations(): List<LocationEntity>?

    suspend fun deleteLocation(location: LocationEntity)
}
