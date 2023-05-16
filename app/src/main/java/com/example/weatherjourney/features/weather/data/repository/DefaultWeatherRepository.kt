package com.example.weatherjourney.features.weather.data.repository

import com.example.weatherjourney.features.weather.data.remote.WeatherApi
import com.example.weatherjourney.features.weather.data.remote.dto.AllWeatherDto
import com.example.weatherjourney.features.weather.domain.model.Coordinate
import com.example.weatherjourney.features.weather.domain.repository.WeatherRepository
import com.example.weatherjourney.util.Result
import com.example.weatherjourney.util.runCatching

class DefaultWeatherRepository(
    private val api: WeatherApi,
) : WeatherRepository {

    override suspend fun getAllWeather(
        coordinate: Coordinate,
        timeZone: String,
    ): Result<AllWeatherDto> = runCatching {
        api.getAllWeather(
            lat = coordinate.latitude,
            long = coordinate.longitude,
            timeZone = timeZone,
        )
    }
}
