package com.example.weather.fake.data

import com.example.weather.data.WeatherRepository
import com.example.weather.model.geocoding.Coordinate
import com.example.weather.model.weather.AllWeather
import com.example.weather.util.allWeather1
import com.example.weather.utils.Result

class FakeWeatherRepository(var isSuccess: Boolean = true) : WeatherRepository {

    override suspend fun getWeather(coordinate: Coordinate): Result<AllWeather> {
        return if (isSuccess) {
            Result.Success(allWeather1)
        } else {
            Result.Error(RuntimeException("Boom..."))
        }
    }
}
