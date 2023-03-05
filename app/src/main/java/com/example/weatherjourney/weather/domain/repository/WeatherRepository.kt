package com.example.weatherjourney.weather.domain.repository

import com.example.weatherjourney.util.Result
import com.example.weatherjourney.weather.data.remote.dto.AirQuality
import com.example.weatherjourney.weather.data.remote.dto.AllWeatherDto
import com.example.weatherjourney.weather.domain.model.Coordinate

interface WeatherRepository {

    suspend fun getAllWeather(
        coordinate: Coordinate,
        timeZone: String
    ): Result<AllWeatherDto>

    suspend fun getAirQuality(
        coordinate: Coordinate,
        timeZone: String
    ): Result<AirQuality>
}
