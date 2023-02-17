package com.example.weatherjourney.weather.domain.usecase

import com.example.weatherjourney.weather.domain.model.Coordinate
import com.example.weatherjourney.weather.domain.repository.LocationRepository

class DeleteLocation(private val repository: LocationRepository) {

    suspend operator fun invoke(coordinate: Coordinate) =
        repository.getLocation(coordinate)?.let { repository.deleteLocation(it) }
}
