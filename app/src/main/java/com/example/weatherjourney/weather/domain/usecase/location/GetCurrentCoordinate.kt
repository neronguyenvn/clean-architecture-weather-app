package com.example.weatherjourney.weather.domain.usecase.location

import com.example.weatherjourney.util.Result
import com.example.weatherjourney.weather.domain.model.Coordinate
import com.example.weatherjourney.weather.domain.repository.LocationRepository

class GetCurrentCoordinate(private val repository: LocationRepository) {

    suspend operator fun invoke(): Result<Coordinate> = repository.getCurrentCoordinate()
}
