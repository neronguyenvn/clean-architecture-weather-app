package com.example.weatherjourney.fake.data

import com.example.weatherjourney.model.data.Coordinate
import com.example.weatherjourney.util.Result
import com.example.weatherjourney.util.allWeather1
import com.example.weatherjourney.weather.data.remote.dto.AllWeatherDto
import com.example.weatherjourney.weather.domain.repository.WeatherRepository

class FakeWeatherRepository(var isSuccess: Boolean = true) : WeatherRepository {

    override suspend fun fetchAllWeather(coordinate: Coordinate): Result<AllWeatherDto> {
        return if (isSuccess) {
            Result.Success(allWeather1)
        } else {
            Result.Error(RuntimeException("Boom..."))
        }
    }
}
