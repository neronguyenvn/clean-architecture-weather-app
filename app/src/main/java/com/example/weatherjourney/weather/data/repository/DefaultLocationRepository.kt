package com.example.weatherjourney.weather.data.repository

import com.example.weatherjourney.di.DefaultDispatcher
import com.example.weatherjourney.util.Result
import com.example.weatherjourney.weather.data.mapper.toCoordinate
import com.example.weatherjourney.weather.data.mapper.toLocation
import com.example.weatherjourney.weather.data.mapper.toSuggestionCity
import com.example.weatherjourney.weather.data.mapper.toUnifiedCoordinate
import com.example.weatherjourney.weather.data.source.LocationDataSource
import com.example.weatherjourney.weather.data.source.local.entity.LocationEntity
import com.example.weatherjourney.weather.domain.model.Coordinate
import com.example.weatherjourney.weather.domain.model.SuggestionCity
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

    override suspend fun getCityByCoordinate(
        coordinate: Coordinate,
        forceCache: Boolean
    ): Result<String> {
        return if (forceCache) {
            when (
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
        } else {
            locationRemoteDataSource.getCityName(coordinate)
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

    override suspend fun fetchSuggestionLocations(city: String): Result<List<SuggestionCity>> =
        when (val suggestions = locationRemoteDataSource.fetchSuggestionLocations(city)) {
            is Result.Success -> Result.Success(suggestions.data.results.map { it.toSuggestionCity() })
            is Result.Error -> suggestions
        }

    override suspend fun checkIsLocationSaved(coordinate: Coordinate): Boolean =
        when (locationLocalDataSource.getCityName(coordinate.toUnifiedCoordinate())) {
            is Result.Success -> true
            is Result.Error -> false
        }

    override suspend fun saveLocation(city: String, coordinate: Coordinate) {
        locationLocalDataSource.saveLocation(coordinate.toUnifiedCoordinate().toLocation(city))
    }

    override suspend fun getLocations(): List<LocationEntity> = locationLocalDataSource.getLocations()

    private suspend fun updateLocationFromRemote(coordinate: Coordinate) {
        when (val city = locationRemoteDataSource.getCityName(coordinate)) {
            is Result.Success -> locationLocalDataSource.saveLocation(
                coordinate.toUnifiedCoordinate().toLocation(city.data)
            )

            is Result.Error -> throw city.exception
        }
    }
}
