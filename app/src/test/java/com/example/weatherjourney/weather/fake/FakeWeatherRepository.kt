package com.example.weatherjourney.weather.fake

import com.example.weatherjourney.features.weather.data.remote.dto.AllWeatherDto
import com.example.weatherjourney.features.weather.domain.model.Coordinate
import com.example.weatherjourney.features.weather.domain.repository.WeatherRepository
import com.example.weatherjourney.util.Result
import com.example.weatherjourney.weather.allWeatherDto1
import java.net.UnknownHostException

class FakeWeatherRepository : WeatherRepository {

    var haveInternet = true
    var allWeatherDto: AllWeatherDto = allWeatherDto1

    override suspend fun getAllWeather(
        coordinate: Coordinate,
        timeZone: String,
    ): Result<AllWeatherDto> {
        return if (haveInternet) {
            Result.Success(allWeatherDto)
        } else {
            Result.Error(UnknownHostException())
        }
    }
}
