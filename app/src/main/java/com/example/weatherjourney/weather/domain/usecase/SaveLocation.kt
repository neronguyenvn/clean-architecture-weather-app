package com.example.weatherjourney.weather.domain.usecase

import com.example.weatherjourney.weather.data.mapper.toLocationEntity
import com.example.weatherjourney.weather.data.mapper.toUnifiedCoordinate
import com.example.weatherjourney.weather.domain.model.Coordinate
import com.example.weatherjourney.weather.domain.repository.LocationRepository

class SaveLocation(private val repository: LocationRepository) {

    suspend operator fun invoke(
        cityAddress: String,
        coordinate: Coordinate,
        timeZone: String
    ) {
        repository.saveLocation(
            coordinate.toUnifiedCoordinate().toLocationEntity(cityAddress, timeZone)
        )
    }
}
