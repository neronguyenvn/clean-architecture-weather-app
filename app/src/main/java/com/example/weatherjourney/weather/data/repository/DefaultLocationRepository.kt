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
import kotlinx.coroutines.flow.first
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

    override suspend fun fetchCity(coordinate: Coordinate): Result<String> {
        return when (val location = getLocation(coordinate)) {
            null -> {
                try {
                    updateCurrentLocationFromRemote(coordinate)
                } catch (ex: Exception) {
                    return Result.Error(ex)
                }

                Result.Success(
                    dao.getLocation(coordinate.lat, coordinate.long).first().cityAddress
                )
            }

            else -> Result.Success(location.cityAddress)
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

    override suspend fun getSuggestionLocations(cityAddress: String): Result<List<SuggestionCity>> =
        runCatching {
            api.getForwardGeocoding(cityAddress = cityAddress).results.map { it.toSuggestionCity() }
        }

    override suspend fun getLocation(coordinate: Coordinate): LocationEntity? =
        dao.getLocation(coordinate.lat, coordinate.long).firstOrNull()

    override suspend fun saveLocation(location: LocationEntity) {
        dao.insert(location)
    }

    override suspend fun getLocations(): List<LocationEntity>? =
        dao.getLocations().firstOrNull()

    override suspend fun deleteLocation(location: LocationEntity) =
        dao.delete(location)

    private suspend fun updateCurrentLocationFromRemote(coordinate: Coordinate) {
        try {
            val response = api.getReverseGeocoding(coordinate.toApiCoordinate())

            withContext(ioDispatcher) {
                this.launch {
                    preferences.saveTimeZone(response.getTimeZone())
                }
            }

            dao.insert(
                LocationEntity(
                    cityAddress = response.getCityAddress(),
                    lat = coordinate.lat,
                    long = coordinate.long,
                    timeZone = response.getTimeZone(),
                    isCurrentLocation = true
                )
            )
        } catch (ex: Exception) {
            throw ex
        }
    }
}
