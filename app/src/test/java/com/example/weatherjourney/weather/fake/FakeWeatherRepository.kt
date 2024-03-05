package com.example.weatherjourney.weather.fake

import com.example.weatherjourney.core.common.util.Result
import com.example.weatherjourney.core.data.WeatherRepository
import com.example.weatherjourney.core.model.location.Coordinate
import com.example.weatherjourney.core.network.model.NetworkAllWeather
import com.example.weatherjourney.weather.allWeatherDto1
import java.net.UnknownHostException

class FakeWeatherRepository : WeatherRepository {

    var haveInternet = true
    var allWeatherDto: NetworkAllWeather = allWeatherDto1

    override suspend fun getAllWeather(
        coordinate: Coordinate,
        timeZone: String,
    ): Result<NetworkAllWeather> {
        return if (haveInternet) {
            Result.Success(allWeatherDto)
        } else {
            Result.Error(UnknownHostException())
        }
    }
}
