package com.example.weatherjourney.features.weather.domain.repository

import com.example.weatherjourney.features.weather.data.remote.dto.AllWeatherDto
import com.example.weatherjourney.features.weather.domain.model.Coordinate
import com.example.weatherjourney.util.Result

interface WeatherRepository {

    suspend fun getAllWeather(
        coordinate: Coordinate,
        timeZone: String
    ): Result<AllWeatherDto>
}
