package com.example.weatherjourney.weather.data.repository

import com.example.weatherjourney.util.Result
import com.example.weatherjourney.util.runCatching
import com.example.weatherjourney.weather.data.source.remote.ApiService
import com.example.weatherjourney.weather.data.source.remote.dto.AllWeather
import com.example.weatherjourney.weather.domain.model.Coordinate
import com.example.weatherjourney.weather.domain.repository.WeatherRepository

class DefaultWeatherRepository(
    private val apiService: ApiService
) : WeatherRepository {

    override suspend fun fetchAllWeather(coordinate: Coordinate): Result<AllWeather> =
        runCatching {
            apiService.getAllWeather(
                latitude = coordinate.latitude,
                longitude = coordinate.longitude
            )
        }
}
