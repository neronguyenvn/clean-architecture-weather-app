package com.example.weather.data

import com.example.weather.di.IoDispatcher
import com.example.weather.model.geocoding.Location
import com.example.weather.model.weather.AllWeather
import com.example.weather.network.ApiService
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

/**
 * Interface for Repository of Weather DataType
 */
interface WeatherRepository {
    /**
     *  Get the Current Location of the Device thank to Location Repository
     */
    suspend fun getCurrentLocation(): Location

    /**
     *  Get the All Weather by call Api and send CityName
     */
    suspend fun getWeather(city: String): AllWeather

    /**
     *  Get the All Weather by call Api and send Location
     */
    suspend fun getWeather(location: Location): AllWeather

    /**
     *  Get the CityName by call Api and send Location
     */
    suspend fun getCityByLocation(location: Location): String
}

/**
 * Implementation of Interface for Repository of Weather DataType
 */
class DefaultWeatherRepository(
    private val geocodingRepository: GeocodingRepository,
    private val locationRepository: LocationRepository,
    private val apiService: ApiService,
    @IoDispatcher private val dispatcher: CoroutineDispatcher
) : WeatherRepository {

    override suspend fun getCurrentLocation(): Location {
        return locationRepository.getCurrentLocation()
    }

    override suspend fun getCityByLocation(location: Location): String {
        return geocodingRepository.getCity(location)
    }

    override suspend fun getWeather(city: String): AllWeather = withContext(dispatcher) {
        val location = getLocationByCity(city)
        apiService.getWeather(latitude = location.latitude, longitude = location.longitude)
    }

    override suspend fun getWeather(location: Location): AllWeather = withContext(dispatcher) {
        apiService.getWeather(latitude = location.latitude, longitude = location.longitude)
    }

    private suspend fun getLocationByCity(city: String): Location {
        return geocodingRepository.getLocation(city)
    }
}
