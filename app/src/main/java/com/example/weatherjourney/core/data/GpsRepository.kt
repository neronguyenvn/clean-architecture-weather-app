package com.example.weatherjourney.core.data

import com.example.weatherjourney.core.common.util.Result
import com.example.weatherjourney.core.model.Coordinate
import kotlinx.coroutines.flow.Flow

interface GpsRepository {
    fun getCurrentCoordinateStream(): Flow<Result<Coordinate>>
}
