package com.example.weatherjourney.features.weather.domain.usecase.location

import com.example.weatherjourney.domain.AppPreferences
import com.example.weatherjourney.features.weather.domain.mapper.coordinate
import com.example.weatherjourney.features.weather.domain.model.Coordinate
import com.example.weatherjourney.features.weather.domain.repository.LocationRepository
import kotlinx.coroutines.flow.first

class ShouldSaveLocation(
    private val repository: LocationRepository,
    private val appPreferences: AppPreferences
) {

    suspend operator fun invoke(coordinate: Coordinate): Boolean {
        // Shouldn't if this location was already in the database
        if (repository.getLocation(coordinate) != null) return false

        // Shouldn't if the weather info of this location is gonna showed in the UI
        if (appPreferences.locationPreferencesFlow.first().coordinate == coordinate) return false

        return true
    }
}
