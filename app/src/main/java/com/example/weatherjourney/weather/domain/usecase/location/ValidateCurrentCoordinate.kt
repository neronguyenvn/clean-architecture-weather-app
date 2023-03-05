package com.example.weatherjourney.weather.domain.usecase.location

import com.example.weatherjourney.util.Result
import com.example.weatherjourney.weather.data.mapper.coordinate
import com.example.weatherjourney.weather.data.mapper.toUnifiedCoordinate
import com.example.weatherjourney.weather.domain.repository.LocationRepository

class ValidateCurrentCoordinate(private val repository: LocationRepository) {

    suspend operator fun invoke(): Boolean {
        val currentCoordinateResult = repository.getCurrentCoordinate()

        if (currentCoordinateResult is Result.Error) {
            return false
        }

        val currentCoordinate = (currentCoordinateResult as Result.Success).data

        val currentLocation = repository.getCurrentLocation()

        if (currentLocation == null) {
            repository.fetchLocation(currentCoordinate.toUnifiedCoordinate())
            return true
        }

        if (currentLocation.coordinate != currentCoordinate.toUnifiedCoordinate()) {
            repository.deleteLocation(currentLocation)
            repository.fetchLocation(currentCoordinate.toUnifiedCoordinate())
            return true
        }

        return false
    }
}
