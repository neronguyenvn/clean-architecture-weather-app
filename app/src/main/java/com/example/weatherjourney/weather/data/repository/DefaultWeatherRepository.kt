package com.example.weatherjourney.weather.data.repository

import com.example.weatherjourney.util.Result
import com.example.weatherjourney.util.runCatching
import com.example.weatherjourney.weather.data.remote.Api
import com.example.weatherjourney.weather.data.remote.dto.AirQuality
import com.example.weatherjourney.weather.data.remote.dto.AllWeather
import com.example.weatherjourney.weather.domain.model.Coordinate
import com.example.weatherjourney.weather.domain.repository.WeatherRepository

class DefaultWeatherRepository(
    private val apiService: Api
) : WeatherRepository {

    override suspend fun fetchAllWeather(
        coordinate: Coordinate,
        timeZone: String,
        temperatureUnit: String,
        windSpeedUnit: String,
        forceCache: Boolean
    ): Result<AllWeather> = runCatching {
        apiService.getAllWeather(
            lat = coordinate.lat,
            long = coordinate.long,
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
            lat = coordinate.lat,
            long = coordinate.long,
            timeZone = timeZone
        )
    }
}
