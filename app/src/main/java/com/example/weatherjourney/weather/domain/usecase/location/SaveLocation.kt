package com.example.weatherjourney.weather.domain.usecase.location

import com.example.weatherjourney.LocationPreferences
import com.example.weatherjourney.util.Result
import com.example.weatherjourney.weather.data.local.entity.LocationEntity
import com.example.weatherjourney.weather.domain.mapper.toCoordinate
import com.example.weatherjourney.weather.domain.repository.LocationRepository

class SaveLocation(
    private val repository: LocationRepository
) {

    suspend operator fun invoke(location: LocationPreferences, countryCode: String) {
        val coordinate = location.coordinate.toCoordinate()
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
