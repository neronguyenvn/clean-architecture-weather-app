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
     *  Get the Current Location of the Device thank to Location Repository
     */
    suspend fun getCurrentCoordinate(): Coordinate

    /**
     *  Get the All Weather by call Api and send CityName
     */
    suspend fun getWeather(city: String, forceUpdate: Boolean): Result<AllWeather>

    /**
     *  Get the All Weather by call Api and send Location
     */
    suspend fun getWeather(coordinate: Coordinate): Result<AllWeather>

    /**
     *  Get the CityName by call Api and send Location
     */
    suspend fun getCityByCoordinate(coordinate: Coordinate, forceUpdate: Boolean): Result<String>
}

/**
 * Implementation for Repository of Weather DataType
 */
class DefaultWeatherRepository(
    private val locationRepository: LocationRepository,
    private val apiService: ApiService,
    @IoDispatcher private val dispatcher: CoroutineDispatcher
) : WeatherRepository {

    override suspend fun getCurrentCoordinate(): Coordinate {
        return locationRepository.getCurrentCoordinate()
    }

    override suspend fun getCityByCoordinate(
        coordinate: Coordinate,
        forceUpdate: Boolean
    ): Result<String> {
        return locationRepository.getCityByCoordinate(coordinate, forceUpdate)
    }

    override suspend fun getWeather(city: String, forceUpdate: Boolean): Result<AllWeather> {
        return withContext(dispatcher) {
            try {
                val coordinate = getCoordinateByCity(city, forceUpdate)
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

    private suspend fun getCoordinateByCity(
        city: String,
        forceUpdate: Boolean
    ): Coordinate {
        when (val coordinate = locationRepository.getCoordinateByCity(city, forceUpdate)) {
            is Success -> return coordinate.data
            is Error -> throw coordinate.exception
        }
    }
}
