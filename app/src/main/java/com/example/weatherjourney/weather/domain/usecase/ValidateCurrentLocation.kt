package com.example.weatherjourney.weather.domain.usecase

import com.example.weatherjourney.weather.data.mapper.toCoordinate
import com.example.weatherjourney.weather.domain.model.Coordinate
import com.example.weatherjourney.weather.domain.repository.LocationRepository

class ValidateCurrentLocation(private val repository: LocationRepository) {

    suspend operator fun invoke(coordinate: Coordinate): Boolean =
        repository.getCurrentLocation()?.let { it.toCoordinate() == coordinate } ?: false
}
