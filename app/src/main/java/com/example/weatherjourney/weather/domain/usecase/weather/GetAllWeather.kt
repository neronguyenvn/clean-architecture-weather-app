package com.example.weatherjourney.weather.domain.usecase.weather

import com.example.weatherjourney.domain.PreferenceRepository
import com.example.weatherjourney.weather.domain.model.Coordinate
import com.example.weatherjourney.weather.domain.repository.WeatherRepository
import kotlinx.coroutines.flow.first

class GetAllWeather(
    private val repository: WeatherRepository,
    private val preferences: PreferenceRepository
) {

    suspend operator fun invoke(coordinate: Coordinate, timeZone: String) = repository.getAllWeather(
        coordinate,
        timeZone,
        preferences.temperatureUnitFlow.first().apiParam,
        preferences.windSpeedUnitFlow.first().apiParam
    )
}
