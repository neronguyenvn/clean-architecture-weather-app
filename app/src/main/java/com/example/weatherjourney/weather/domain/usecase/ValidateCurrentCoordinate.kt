package com.example.weatherjourney.weather.domain.usecase

import com.example.weatherjourney.util.Result
import com.example.weatherjourney.weather.data.mapper.toCoordinate
import com.example.weatherjourney.weather.data.mapper.toUnifiedCoordinate
import com.example.weatherjourney.weather.domain.repository.LocationRepository

class ValidateCurrentCoordinate(private val repository: LocationRepository) {

    suspend operator fun invoke() {
        val currentCoordinateResult = repository.getCurrentCoordinate()

        if (currentCoordinateResult is Result.Error) return

        val currentCoordinate = (currentCoordinateResult as Result.Success).data

        val currentLocation = repository.getCurrentLocation()

        if (currentLocation == null) {
            repository.fetchLocation(currentCoordinate.toUnifiedCoordinate())
            return
        }

        if (currentLocation.toCoordinate() != currentCoordinate) {
            repository.deleteLocation(currentLocation)
            repository.fetchLocation(currentCoordinate.toUnifiedCoordinate())
        }
    }
}
