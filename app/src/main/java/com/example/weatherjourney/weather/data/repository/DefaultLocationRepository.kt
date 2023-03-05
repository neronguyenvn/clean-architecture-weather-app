package com.example.weatherjourney.weather.data.repository

import com.example.weatherjourney.di.DefaultDispatcher
import com.example.weatherjourney.di.IoDispatcher
import com.example.weatherjourney.domain.PreferenceRepository
import com.example.weatherjourney.util.Result
import com.example.weatherjourney.util.runCatching
import com.example.weatherjourney.weather.data.local.LocationDao
import com.example.weatherjourney.weather.data.local.entity.LocationEntity
import com.example.weatherjourney.weather.data.mapper.toApiCoordinate
import com.example.weatherjourney.weather.data.mapper.toCoordinate
import com.example.weatherjourney.weather.data.mapper.toSuggestionCity
import com.example.weatherjourney.weather.data.remote.Api
import com.example.weatherjourney.weather.domain.model.Coordinate
import com.example.weatherjourney.weather.domain.model.SuggestionCity
import com.example.weatherjourney.weather.domain.repository.LocationRepository
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.Tasks.await
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class DefaultLocationRepository(
    private val dao: LocationDao,
    private val api: Api,
    private val client: FusedLocationProviderClient,
    private val preferences: PreferenceRepository,
    @DefaultDispatcher private val defaultDispatcher: CoroutineDispatcher,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : LocationRepository {

    override suspend fun fetchLocation(coordinate: Coordinate): Result<Any> =
        when (getLocation(coordinate)) {
            null -> {
                try {
                    updateCurrentLocationFromRemote(coordinate)
                } catch (ex: Exception) {
                    Result.Error(ex)
                }

                Result.Success(true)
            }

            else -> Result.Success(true)
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

    override suspend fun getSuggestionLocations(cityAddress: String): Result<List<SuggestionCity>> =
        runCatching {
            api.getForwardGeocoding(cityAddress = cityAddress).results.map { it.toSuggestionCity() }
        }

    override suspend fun getLocation(coordinate: Coordinate): LocationEntity? =
        dao.observeLocation(coordinate.latitude, coordinate.longitude).firstOrNull()

    override suspend fun getCurrentLocation(): LocationEntity? =
        dao.observeCurrentLocation().firstOrNull()

    override fun getLocationsStream(): Flow<List<LocationEntity>> =
        dao.observeLocations()

    override suspend fun saveLocation(location: LocationEntity) = dao.insertLocation(location)

    override suspend fun deleteLocation(location: LocationEntity) =
        dao.deleteLocation(location)

    private suspend fun updateCurrentLocationFromRemote(coordinate: Coordinate) {
        try {
            val response = api.getReverseGeocoding(coordinate.toApiCoordinate())

            preferences.updateLocation(
                response.getCityAddress(),
                coordinate,
                response.getTimeZone()
            )

            withContext(ioDispatcher) {
                launch {
                    dao.insertLocation(
                        LocationEntity(
                            cityAddress = response.getCityAddress(),
                            latitude = coordinate.latitude,
                            longitude = coordinate.longitude,
                            timeZone = response.getTimeZone(),
                            isCurrentLocation = true
                        )
                    )
                }
            }
        } catch (ex: Exception) {
            throw ex
        }
    }
}
