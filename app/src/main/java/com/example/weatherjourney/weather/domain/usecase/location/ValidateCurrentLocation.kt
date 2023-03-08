package com.example.weatherjourney.weather.domain.usecase.location

import com.example.weatherjourney.util.Result
import com.example.weatherjourney.weather.data.mapper.coordinate
import com.example.weatherjourney.weather.domain.repository.LocationRepository

class ValidateCurrentLocation(private val repository: LocationRepository) {

    suspend operator fun invoke() {
        val currentCoordinateResult = repository.getCurrentCoordinate()

        // Location permission is denied
        if (currentCoordinateResult is Result.Error) {
            return
        }

        val currentCoordinate = (currentCoordinateResult as Result.Success).data

        val currentLocation = repository.getCurrentLocation()

        // Update current location in database
        if (currentLocation == null) {
            repository.fetchLocation(currentCoordinate, false)
            return
        }

        // Update current location in database
        if (currentLocation.coordinate != currentCoordinate) {
            repository.deleteLocation(currentLocation)
            repository.fetchLocation(currentCoordinate, false)
        }
    }
}
