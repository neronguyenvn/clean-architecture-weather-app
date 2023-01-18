package com.example.weatherjourney.weather.domain.repository

import com.example.weatherjourney.util.Result
import com.example.weatherjourney.weather.domain.model.Coordinate

interface LocationRepository {

    suspend fun getCoordinateByCity(city: String): Result<Coordinate>

    suspend fun getCityByCoordinate(coordinate: Coordinate): Result<String>

    suspend fun getCurrentCoordinate(): Result<Coordinate>
}
