package com.example.weatherjourney.features.weather.domain.usecase.location

import com.example.weatherjourney.features.weather.data.local.entity.LocationEntity
import com.example.weatherjourney.features.weather.domain.mapper.coordinate
import com.example.weatherjourney.features.weather.domain.repository.LocationRepository
import com.example.weatherjourney.locationpreferences.LocationPreferences
import com.example.weatherjourney.util.Result

class SaveLocation(
    private val repository: LocationRepository
) {

    suspend operator fun invoke(location: LocationPreferences, countryCode: String) {
        val coordinate = location.coordinate
        val currentCoordinate = repository.getCurrentCoordinate()
            .let { if (it is Result.Success) it.data else null }

        repository.saveLocation(
            LocationEntity(
                cityAddress = location.cityAddress,
                latitude = coordinate.latitude,
                longitude = coordinate.longitude,
                timeZone = location.timeZone,
                isCurrentLocation = currentCoordinate?.let { it == coordinate } ?: false,
                countryCode = countryCode
            )
        )
    }
}
