package com.example.weather.data

import com.example.weather.model.database.Location
import com.example.weather.model.geocoding.Coordinate
import com.example.weather.network.ApiService
import com.example.weather.utils.Result
import com.example.weather.utils.Result.Error
import com.example.weather.utils.Result.Success
import com.example.weather.utils.toCoordinate
import kotlinx.coroutines.flow.first
import kotlinx.serialization.SerializationException
import retrofit2.HttpException
import java.net.UnknownHostException

/**
 * Interface for Data Source of Location DataType.
 */
interface LocationDataSource {

    /**
     * Get Coordinate by call Api or query Local Data Source.
     */
    suspend fun getCoordinate(city: String): Result<Coordinate>

    /**
     * Get CityName by call Api or query Local Data Source.
     */
    suspend fun getCityName(coordinate: Coordinate): Result<String>

    /**
     * Save Location in Local Data Source.
     */
    suspend fun saveLocation(location: Location)
}

/**
 * Implementation for Remote Data Source of Location DataType.
 */
class LocationRemoteDataSource(private val apiService: ApiService) : LocationDataSource {
    override suspend fun getCoordinate(city: String): Result<Coordinate> {
        return try {
            val result = apiService.getForwardGeocoding(city)
            Success(result.results.first().coordinate)
        } catch (ex: UnknownHostException) {
            Error(UnknownHostException("No internet connection"))
        } catch (ex: NoSuchElementException) {
            Error(NoSuchElementException("Invalid location"))
        } catch (ex: SerializationException) {
            Error(SerializationException("Json response is malformed"))
        } catch (ex: HttpException) {
            Error(ex)
        }
    }

    override suspend fun getCityName(coordinate: Coordinate): Result<String> {
        return try {
            val result = apiService.getReverseGeocoding(
                "${coordinate.latitude}+${coordinate.longitude}"
            )
            Success(result.results.first().components.city)
        } catch (ex: UnknownHostException) {
            Error(UnknownHostException("No internet connection"))
        } catch (ex: SerializationException) {
            Error(SerializationException("Json response is malformed"))
        }
    }

    override suspend fun saveLocation(location: Location) {
        // Not required for the remote data source
    }
}

/**
 * Implementation for Local Data Source of Location DataType.
 */
class LocationLocalDataSource(private val locationDao: LocationDao) : LocationDataSource {
    override suspend fun getCoordinate(city: String): Result<Coordinate> {
        val location = locationDao.getLocationByCity(city).first()
        return try {
            Success(location.toCoordinate())
        } catch (ex: NullPointerException) {
            Error(NullPointerException("Couldn't find any coordinate with input city"))
        }
    }

    override suspend fun getCityName(coordinate: Coordinate): Result<String> {
        val location =
            locationDao.getLocationByCoordinate(coordinate.latitude, coordinate.longitude).first()
        return try {
            Success(location.city)
        } catch (ex: NullPointerException) {
            Error(NullPointerException("Couldn't find any city with input coordinate"))
        }
    }

    override suspend fun saveLocation(location: Location) {
        locationDao.insertLocation(location)
    }
}
