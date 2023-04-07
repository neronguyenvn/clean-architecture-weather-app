package com.example.weatherjourney.features.weather.domain.usecase.location

import com.example.weatherjourney.features.weather.domain.model.Coordinate
import com.example.weatherjourney.features.weather.domain.repository.LocationRepository

class DeleteLocation(private val repository: LocationRepository) {

    suspend operator fun invoke(coordinate: Coordinate) =
        repository.getLocation(coordinate)?.let { repository.deleteLocation(it) }

    suspend operator fun invoke(shouldDeleteCurrentLocation: Boolean) {
        if (!shouldDeleteCurrentLocation) return
        repository.getCurrentLocation()?.let { repository.deleteLocation(it) }
    }
}
