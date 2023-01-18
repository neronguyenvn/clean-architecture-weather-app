package com.example.weatherjourney.weather.domain.repository

import com.example.weatherjourney.util.Result
import com.example.weatherjourney.weather.data.source.remote.dto.AllWeather
import com.example.weatherjourney.weather.domain.model.Coordinate

interface WeatherRepository {

    suspend fun fetchAllWeather(coordinate: Coordinate): Result<AllWeather>
}
