package com.example.weatherjourney.weather.data.repository

import com.example.weatherjourney.util.Result
import com.example.weatherjourney.util.runCatching
import com.example.weatherjourney.weather.data.remote.Api
import com.example.weatherjourney.weather.data.remote.dto.AirQuality
import com.example.weatherjourney.weather.data.remote.dto.AllWeatherDto
import com.example.weatherjourney.weather.domain.model.Coordinate
import com.example.weatherjourney.weather.domain.repository.WeatherRepository

class DefaultWeatherRepository(
    private val apiService: Api
) : WeatherRepository {

    override suspend fun getAllWeather(
        coordinate: Coordinate,
        timeZone: String,
        temperatureUnit: String,
        windSpeedUnit: String
    ): Result<AllWeatherDto> = runCatching {
        apiService.getAllWeather(
            lat = coordinate.latitude,
            long = coordinate.longitude,
            timeZone = timeZone,
            temperatureUnit = temperatureUnit,
            windSpeedUnit = windSpeedUnit
        )
    }

    override suspend fun getAirQuality(
        coordinate: Coordinate,
        timeZone: String
    ): Result<AirQuality> = runCatching {
        apiService.getAirQuality(
            latitude = coordinate.latitude,
            longitude = coordinate.longitude,
            timeZone = timeZone
        )
    }
}
