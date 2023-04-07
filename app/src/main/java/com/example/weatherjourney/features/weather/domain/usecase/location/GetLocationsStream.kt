package com.example.weatherjourney.features.weather.domain.usecase.location

import com.example.weatherjourney.features.weather.data.local.entity.LocationEntity
import com.example.weatherjourney.features.weather.domain.repository.LocationRepository
import kotlinx.coroutines.flow.Flow

class GetLocationsStream(private val repository: LocationRepository) {

    operator fun invoke(): Flow<List<LocationEntity>> = repository.getLocationsStream()
}
