package com.example.weatherjourney.weather.domain.usecase.location

import com.example.weatherjourney.locationpreferences.LocationPreferences
import com.example.weatherjourney.util.Result
import com.example.weatherjourney.weather.data.mapper.coordinate
import com.example.weatherjourney.weather.domain.mapper.roundTo
import com.example.weatherjourney.weather.domain.mapper.toCoordinate
import com.example.weatherjourney.weather.domain.repository.LocationRepository

class ValidateCurrentLocation(private val repository: LocationRepository) {

    suspend operator fun invoke(): Result<Boolean> {
        val currentCoordinate = when (val result = repository.getCurrentCoordinate()) {
            is Result.Success -> result.data
            is Result.Error -> return result
        }

        val currentLocation = repository.getCurrentLocation()
            // If current location in db is null, fetch it with currentCoordinate
            ?: return when (
                val result =
                    repository.fetchCurrentLocationIfNeeded(currentCoordinate)
            ) {
                is Result.Success -> Result.Success(true)
                is Result.Error -> result
            }

        if (currentLocation.coordinate != currentCoordinate) {
            repository.deleteLocation(currentLocation)
            return when (val result = repository.fetchCurrentLocationIfNeeded(currentCoordinate)) {
                is Result.Success -> Result.Success(true)
                is Result.Error -> result
            }
        }

        return Result.Success(true)
    }

    suspend operator fun invoke(location: LocationPreferences): Result<Boolean> {
        if (location == LocationPreferences.getDefaultInstance()) {
            val currentCoordinate = when (val result = repository.getCurrentCoordinate()) {
                is Result.Success -> result.data
                is Result.Error -> return result
            }

            return when (val result = repository.fetchCurrentLocationIfNeeded(currentCoordinate)) {
                is Result.Success -> {
                    repository.updateLastLocationFromCurrentOne()
                    Result.Success(true)
                }

                is Result.Error -> result
            }
        } else {
            val currentCoordinate = repository.getCurrentLocation()?.coordinate
                ?: return Result.Success(false)

            return Result.Success(
                currentCoordinate.roundTo(1) == location.coordinate.toCoordinate().roundTo(1)
            )
        }
    }
}
