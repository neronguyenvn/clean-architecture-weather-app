package com.example.weatherjourney.domain

import com.example.weatherjourney.weather.domain.model.Coordinate
import kotlinx.coroutines.flow.Flow

interface PreferenceRepository {
    val coordinateFlow: Flow<Coordinate>
    suspend fun saveCoordinate(coordinate: Coordinate)
}
