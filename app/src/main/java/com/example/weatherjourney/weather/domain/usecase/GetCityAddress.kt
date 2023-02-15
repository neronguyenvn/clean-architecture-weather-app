package com.example.weatherjourney.weather.domain.usecase

import com.example.weatherjourney.util.Result
import com.example.weatherjourney.weather.data.mapper.toUnifiedCoordinate
import com.example.weatherjourney.weather.domain.model.Coordinate
import com.example.weatherjourney.weather.domain.repository.LocationRepository

class GetCityAddress(private val repository: LocationRepository) {

    suspend operator fun invoke(coordinate: Coordinate): Result<String> =
        repository.fetchCity(coordinate.toUnifiedCoordinate())
}
