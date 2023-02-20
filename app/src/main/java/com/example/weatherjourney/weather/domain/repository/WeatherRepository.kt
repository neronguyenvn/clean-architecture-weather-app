package com.example.weatherjourney.weather.domain.repository

import com.example.weatherjourney.util.Result
import com.example.weatherjourney.weather.data.remote.dto.AirQuality
import com.example.weatherjourney.weather.data.remote.dto.AllWeather
import com.example.weatherjourney.weather.domain.model.Coordinate

interface WeatherRepository {

    suspend fun fetchAllWeather(
        coordinate: Coordinate,
        timeZone: String,
        temperatureUnit: String,
        windSpeedUnit: String,
        forceCache: Boolean
    ): Result<AllWeather>

    suspend fun getAirQuality(
        coordinate: Coordinate,
        timeZone: String
    ): Result<AirQuality>
}
