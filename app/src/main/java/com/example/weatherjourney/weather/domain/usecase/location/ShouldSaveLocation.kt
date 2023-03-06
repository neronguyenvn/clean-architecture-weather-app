package com.example.weatherjourney.weather.domain.usecase.location

import com.example.weatherjourney.domain.PreferenceRepository
import com.example.weatherjourney.weather.domain.mapper.toCoordinate
import com.example.weatherjourney.weather.domain.model.Coordinate
import com.example.weatherjourney.weather.domain.repository.LocationRepository
import kotlinx.coroutines.flow.first

class ShouldSaveLocation(
    private val repository: LocationRepository,
    private val preferences: PreferenceRepository
) {

    suspend operator fun invoke(coordinate: Coordinate): Boolean {
        // Shouldn't if this location was already in the database
        if (repository.getLocation(coordinate) != null) return false

        // Shouldn't if the weather info of this location is gonna showed in the UI
        if (preferences.locationPreferencesFlow.first().coordinate.toCoordinate() == coordinate) return false

        return true
    }
}
