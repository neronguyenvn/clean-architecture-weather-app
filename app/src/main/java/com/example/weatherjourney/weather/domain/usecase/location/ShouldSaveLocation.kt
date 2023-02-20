package com.example.weatherjourney.weather.domain.usecase.location

import com.example.weatherjourney.domain.PreferenceRepository
import com.example.weatherjourney.weather.data.mapper.toUnifiedCoordinate
import com.example.weatherjourney.weather.domain.model.Coordinate
import com.example.weatherjourney.weather.domain.repository.LocationRepository

class ShouldSaveLocation(
    private val repository: LocationRepository,
    private val preferences: PreferenceRepository
) {

    suspend operator fun invoke(coordinate: Coordinate, cityAddress: String): Boolean {
        if (repository.getLocation(coordinate.toUnifiedCoordinate()) != null) return false
        if (preferences.getLastCityAddress() == cityAddress) return false
        return true
    }
}
