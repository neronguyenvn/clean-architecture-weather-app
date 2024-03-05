package com.example.weatherjourney.core.data.implementation

import com.example.weatherjourney.core.common.util.Result
import com.example.weatherjourney.core.common.util.runCatching
import com.example.weatherjourney.core.data.WeatherRepository
import com.example.weatherjourney.core.model.location.Coordinate
import com.example.weatherjourney.core.network.WtnNetworkDataSource
import com.example.weatherjourney.core.network.model.NetworkAllWeather
import javax.inject.Inject

class DefaultWeatherRepository @Inject constructor(
    private val network: WtnNetworkDataSource,
) : WeatherRepository {
    override suspend fun getAllWeather(
        coordinate: Coordinate,
        timeZone: String,
    ): Result<NetworkAllWeather> = runCatching {
        network.getAllWeather(
            coordinate = coordinate,
            timeZone = timeZone,
        )
    }
}
