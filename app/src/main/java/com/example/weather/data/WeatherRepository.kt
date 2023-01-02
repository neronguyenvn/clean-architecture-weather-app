package com.example.weather.data

import com.example.weather.di.IoDispatcher
import com.example.weather.model.geocoding.Coordinate
import com.example.weather.model.weather.AllWeather
import com.example.weather.network.ApiService
import com.example.weather.utils.Result
import com.example.weather.utils.Result.Error
import com.example.weather.utils.Result.Success
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

/**
 * Interface for Repository of Weather DataType
 */
interface WeatherRepository {

    /**
     *  Get the All Weather by call Api and send Location
     */
    suspend fun getWeather(coordinate: Coordinate): Result<AllWeather>
}

/**
 * Implementation for Repository of Weather DataType
 */
class DefaultWeatherRepository(
    private val apiService: ApiService,
    @IoDispatcher private val dispatcher: CoroutineDispatcher
) : WeatherRepository {

    override suspend fun getWeather(coordinate: Coordinate): Result<AllWeather> {
        return withContext(dispatcher) {
            try {
                Success(
                    apiService.getAllWeather(
                        latitude = coordinate.latitude,
                        longitude = coordinate.longitude
                    )
                )
            } catch (ex: Exception) {
                Error(ex)
            }
        }
    }
}
