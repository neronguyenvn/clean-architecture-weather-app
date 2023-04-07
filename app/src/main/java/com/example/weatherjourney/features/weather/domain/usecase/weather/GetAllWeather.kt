package com.example.weatherjourney.features.weather.domain.usecase.weather

import com.example.weatherjourney.features.weather.domain.model.Coordinate
import com.example.weatherjourney.features.weather.domain.repository.WeatherRepository

class GetAllWeather(
    private val repository: WeatherRepository
) {

    suspend operator fun invoke(coordinate: Coordinate, timeZone: String) =
        repository.getAllWeather(
            coordinate,
            timeZone
        )
}
