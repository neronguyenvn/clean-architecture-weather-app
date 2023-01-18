package com.example.weatherjourney.fake.data

import com.example.weatherjourney.data.WeatherRepository
import com.example.weatherjourney.model.data.Coordinate
import com.example.weatherjourney.util.Result
import com.example.weatherjourney.util.allWeather1
import com.example.weatherjourney.weather.data.source.remote.dto.AllWeather

class FakeWeatherRepository(var isSuccess: Boolean = true) : WeatherRepository {

    override suspend fun fetchAllWeather(coordinate: Coordinate): Result<AllWeather> {
        return if (isSuccess) {
            Result.Success(allWeather1)
        } else {
            Result.Error(RuntimeException("Boom..."))
        }
    }
}
