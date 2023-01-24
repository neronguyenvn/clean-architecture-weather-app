package com.example.weatherjourney.weather.domain.repository

import com.example.weatherjourney.util.Result
import com.example.weatherjourney.weather.data.source.remote.dto.ForwardGeocoding
import com.example.weatherjourney.weather.domain.model.Coordinate

interface LocationRepository {

    suspend fun getCityByCoordinate(coordinate: Coordinate): Result<String>

    suspend fun getCurrentCoordinate(): Result<Coordinate>

    suspend fun fetchSuggestionLocations(city: String): Result<ForwardGeocoding>
}
