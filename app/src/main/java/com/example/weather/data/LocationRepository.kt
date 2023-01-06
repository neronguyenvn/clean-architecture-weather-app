package com.example.weather.data

import android.annotation.SuppressLint
import com.example.weather.di.DefaultDispatcher
import com.example.weather.model.geocoding.Coordinate
import com.example.weather.utils.REAL_LOADING_DELAY_TIME
import com.example.weather.utils.Result
import com.example.weather.utils.Result.Error
import com.example.weather.utils.Result.Success
import com.example.weather.utils.toCoordinate
import com.example.weather.utils.toLocation
import com.example.weather.utils.toUnifiedCoordinate
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.Tasks.await
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import kotlinx.serialization.SerializationException
import retrofit2.HttpException
import java.net.UnknownHostException

/**
 * Interface for Repository of Location DataType.
 */
interface LocationRepository {

    /**
     * Get Coordinate by call Remote Data Source to update Local Data Source if needed then
     * call Local Data Source to get result.
     * @param city CityName will be converted to get Coordinate.
     */
    suspend fun getCoordinateByCity(city: String): Result<Coordinate>

    /**
     * Get CityName by call Remote Data Source to update Local Data Source if needed then
     * call Local Data Source to get result.
     * @param coordinate Location will be converted to get CityName.
     */
    suspend fun getCityByCoordinate(coordinate: Coordinate): Result<String>

    /**
     * Get the Current Location of the Device.
     */
    suspend fun getCurrentCoordinate(): Coordinate
}

/**
 * Implementation for Repository of Location DataType.
 */
class DefaultLocationRepository(
    private val locationLocalDataSource: LocationDataSource,
    private val locationRemoteDataSource: LocationDataSource,
    private val client: FusedLocationProviderClient,
    @DefaultDispatcher private val defaultDispatcher: CoroutineDispatcher
) : LocationRepository {

    override suspend fun getCoordinateByCity(city: String): Result<Coordinate> {
        return when (val coordinate = locationLocalDataSource.getCoordinate(city)) {
            is Success -> {
                // Delay 1 second to make the reload more real
                delay(REAL_LOADING_DELAY_TIME)
                coordinate
            }
            is Error -> {
                try {
                    updateLocationFromRemote(city)
                } catch (ex: Exception) {
                    when (ex) {
                        is UnknownHostException, is NoSuchElementException, is HttpException, is SerializationException -> Error(
                            ex
                        )
                    }
                }
                locationLocalDataSource.getCoordinate(city)
            }
        }
    }

    override suspend fun getCityByCoordinate(coordinate: Coordinate): Result<String> {
        return when (
            val city =
                locationLocalDataSource.getCityName(coordinate.toUnifiedCoordinate())
        ) {
            is Success -> {
                // Delay 1 second to make the reload more real
                delay(REAL_LOADING_DELAY_TIME)
                city
            }
            is Error -> {
                try {
                    updateLocationFromRemote(coordinate)
                } catch (ex: Exception) {
                    when (ex) {
                        is UnknownHostException, is SerializationException -> Error(ex)
                    }
                }
                locationLocalDataSource.getCityName(coordinate.toUnifiedCoordinate())
            }
        }
    }

    @SuppressLint("MissingPermission")
    override suspend fun getCurrentCoordinate(): Coordinate = withContext(defaultDispatcher) {
        val locationTask = client.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null)
        await(locationTask).toCoordinate()
    }

    private suspend fun updateLocationFromRemote(city: String) {
        when (val coordinate = locationRemoteDataSource.getCoordinate(city)) {
            is Success -> locationLocalDataSource.saveLocation(
                coordinate.data.toUnifiedCoordinate().toLocation(city)
            )
            is Error -> throw coordinate.exception
        }
    }

    private suspend fun updateLocationFromRemote(coordinate: Coordinate) {
        when (val city = locationRemoteDataSource.getCityName(coordinate)) {
            is Success -> locationLocalDataSource.saveLocation(
                coordinate.toUnifiedCoordinate().toLocation(city.data)
            )
            is Error -> throw city.exception
        }
    }
}
