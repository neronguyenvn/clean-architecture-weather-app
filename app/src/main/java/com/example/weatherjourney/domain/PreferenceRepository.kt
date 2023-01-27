package com.example.weatherjourney.domain

import com.example.weatherjourney.weather.domain.model.Coordinate

interface PreferenceRepository {

    suspend fun getLastCoordinate(): Coordinate

    suspend fun saveCoordinate(coordinate: Coordinate)
}
