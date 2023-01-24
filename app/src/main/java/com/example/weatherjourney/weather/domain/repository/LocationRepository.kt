package com.example.weatherjourney.weather.domain.repository

import com.example.weatherjourney.util.Result
import com.example.weatherjourney.weather.domain.model.Coordinate
import com.example.weatherjourney.weather.domain.model.SuggestionCity

interface LocationRepository {

    suspend fun getCityByCoordinate(coordinate: Coordinate): Result<String>

    suspend fun getCurrentCoordinate(): Result<Coordinate>

    suspend fun fetchSuggestionLocations(city: String): Result<List<SuggestionCity>>
}
