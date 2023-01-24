package com.example.weatherjourney.weather.data.repository

import com.example.weatherjourney.di.DefaultDispatcher
import com.example.weatherjourney.util.Result
import com.example.weatherjourney.weather.data.mapper.toCoordinate
import com.example.weatherjourney.weather.data.mapper.toLocation
import com.example.weatherjourney.weather.data.mapper.toUnifiedCoordinate
import com.example.weatherjourney.weather.data.source.LocationDataSource
import com.example.weatherjourney.weather.data.source.remote.dto.ForwardGeocoding
import com.example.weatherjourney.weather.domain.model.Coordinate
import com.example.weatherjourney.weather.domain.repository.LocationRepository
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.Tasks.await
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

class DefaultLocationRepository(
    private val locationLocalDataSource: LocationDataSource,
    private val locationRemoteDataSource: LocationDataSource,
    private val client: FusedLocationProviderClient,
    @DefaultDispatcher private val defaultDispatcher: CoroutineDispatcher
) : LocationRepository {

    override suspend fun getCityByCoordinate(coordinate: Coordinate): Result<String> {
        return when (
            val city =
                locationLocalDataSource.getCityName(coordinate.toUnifiedCoordinate())
        ) {
            is Result.Success -> city
            is Result.Error -> {
                try {
                    updateLocationFromRemote(coordinate)
                } catch (ex: Exception) {
                    return Result.Error(ex)
                }
                locationLocalDataSource.getCityName(coordinate.toUnifiedCoordinate())
            }
        }
    }

    override suspend fun getCurrentCoordinate(): Result<Coordinate> =
        withContext(defaultDispatcher) {
            try {
                val locationTask = client.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null)
                Result.Success(await(locationTask).toCoordinate())
            } catch (ex: Exception) {
                Result.Error(ex)
            } catch (ex: SecurityException) {
                Result.Error(ex)
            }
        }

    override suspend fun fetchSuggestionLocations(city: String): Result<ForwardGeocoding> =
        locationRemoteDataSource.fetchSuggestionLocations(city)

    private suspend fun updateLocationFromRemote(coordinate: Coordinate) {
        when (val city = locationRemoteDataSource.getCityName(coordinate)) {
            is Result.Success -> locationLocalDataSource.saveLocation(
                coordinate.toUnifiedCoordinate().toLocation(city.data)
            )
            is Result.Error -> throw city.exception
        }
    }
}
