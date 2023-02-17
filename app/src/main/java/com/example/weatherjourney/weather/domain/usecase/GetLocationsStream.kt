package com.example.weatherjourney.weather.domain.usecase

import com.example.weatherjourney.weather.data.local.entity.LocationEntity
import com.example.weatherjourney.weather.domain.repository.LocationRepository
import kotlinx.coroutines.flow.Flow

class GetLocationsStream(private val repository: LocationRepository) {

    operator fun invoke(): Flow<List<LocationEntity>> = repository.getLocationsStream()
}
