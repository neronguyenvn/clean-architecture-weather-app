package com.example.weatherjourney.weather.domain.usecase

import com.example.weatherjourney.weather.data.local.entity.LocationEntity
import com.example.weatherjourney.weather.domain.repository.LocationRepository

class DeleteLocation(private val repository: LocationRepository) {

    suspend operator fun invoke(location: LocationEntity) =
        repository.deleteLocation(location)
}
