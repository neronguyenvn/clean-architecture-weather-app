package com.example.weather.data

import com.example.weather.model.geocoding.Coordinate
import com.example.weather.model.weather.AllWeather
import com.example.weather.network.ApiService
import com.example.weather.utils.Result
import com.example.weather.utils.Result.Error
import com.example.weather.utils.Result.Success
import java.net.UnknownHostException

/**
 * Interface for Repository of Weather DataType.
 */
interface WeatherRepository {

    /**
     *  Get the All Weather by call Api and send Location.
     */
    suspend fun getWeather(coordinate: Coordinate): Result<AllWeather>
}

/**
 * Implementation for Repository of Weather DataType.
 */
class DefaultWeatherRepository(
    private val apiService: ApiService
) : WeatherRepository {

    override suspend fun getWeather(coordinate: Coordinate): Result<AllWeather> {
        return try {
            val result = apiService.getAllWeather(
                latitude = coordinate.latitude,
                longitude = coordinate.longitude
            )
            Success(result)
        } catch (ex: UnknownHostException) {
            Error(ex)
        }
    }
}
