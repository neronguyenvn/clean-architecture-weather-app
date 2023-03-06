package com.example.weatherjourney.weather.domain.usecase.location

import com.example.weatherjourney.weather.data.mapper.coordinate
import com.example.weatherjourney.weather.domain.model.Coordinate
import com.example.weatherjourney.weather.domain.repository.LocationRepository

class IsCurrentLocation(private val repository: LocationRepository) {

    suspend operator fun invoke(coordinate: Coordinate): Boolean =
        repository.getCurrentLocation()?.let { it.coordinate == coordinate } ?: false
}
