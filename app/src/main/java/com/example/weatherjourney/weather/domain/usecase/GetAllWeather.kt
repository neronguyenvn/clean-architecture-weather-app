package com.example.weatherjourney.weather.domain.usecase

import com.example.weatherjourney.domain.PreferenceRepository
import com.example.weatherjourney.util.Result
import com.example.weatherjourney.weather.data.remote.dto.AllWeather
import com.example.weatherjourney.weather.domain.model.Coordinate
import com.example.weatherjourney.weather.domain.repository.WeatherRepository

class GetAllWeather(
    private val repository: WeatherRepository,
    private val preferences: PreferenceRepository
) {
    suspend operator fun invoke(
        coordinate: Coordinate,
        timeZone: String
    ): Result<AllWeather> = repository.fetchAllWeather(
        coordinate,
        timeZone,
        preferences.getTemperatureUnit().apiParam,
        true
    )
}
