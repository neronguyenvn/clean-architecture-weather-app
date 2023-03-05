package com.example.weatherjourney.weather.domain.usecase.location

import com.example.weatherjourney.util.Result
import com.example.weatherjourney.weather.data.mapper.toUnifiedCoordinate
import com.example.weatherjourney.weather.domain.repository.LocationRepository

class GetAndSaveCurrentLocation(private val repository: LocationRepository) {

    suspend operator fun invoke(): Result<Any> =
        when (val currentCoordinate = repository.getCurrentCoordinate()) {
            is Result.Success -> repository.fetchLocation(currentCoordinate.data.toUnifiedCoordinate())
            else -> currentCoordinate
        }
}
