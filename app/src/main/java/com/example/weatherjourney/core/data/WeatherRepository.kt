package com.example.weatherjourney.core.data

import com.example.weatherjourney.core.common.util.Result
import com.example.weatherjourney.core.model.location.Coordinate
import com.example.weatherjourney.core.network.model.NetworkAllWeather

interface WeatherRepository {

    suspend fun getAllWeather(
        coordinate: Coordinate,
        timeZone: String,
    ): Result<NetworkAllWeather>
}
