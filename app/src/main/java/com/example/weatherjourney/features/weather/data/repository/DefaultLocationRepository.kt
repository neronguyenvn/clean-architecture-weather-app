package com.example.weatherjourney.features.weather.data.repository

import android.Manifest.permission
import android.content.Context
import android.location.LocationManager
import com.example.weatherjourney.features.weather.data.local.LocationDao
import com.example.weatherjourney.features.weather.data.local.entity.LocationEntity
import com.example.weatherjourney.features.weather.data.mapper.toApiCoordinate
import com.example.weatherjourney.features.weather.data.mapper.toCoordinate
import com.example.weatherjourney.features.weather.data.mapper.toSuggestionCity
import com.example.weatherjourney.features.weather.data.remote.WeatherApi
import com.example.weatherjourney.features.weather.domain.model.Coordinate
import com.example.weatherjourney.features.weather.domain.model.SuggestionCity
import com.example.weatherjourney.features.weather.domain.repository.LocationRepository
import com.example.weatherjourney.util.LocationException
import com.example.weatherjourney.util.LocationException.LocationPermissionDeniedException
import com.example.weatherjourney.util.LocationException.LocationServiceDisabledException
import com.example.weatherjourney.util.Result
import com.example.weatherjourney.util.checkPermission
import com.example.weatherjourney.util.runCatching
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.tasks.Tasks.await
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.withContext

class DefaultLocationRepository(
    private val dao: LocationDao,
    private val api: WeatherApi,
    private val client: FusedLocationProviderClient,
    private val context: Context,
    private val defaultDispatcher: CoroutineDispatcher,
) : LocationRepository {

    override suspend fun checkAndUpdateCurrentLocationIfNeeded(
        currentCoordinate: Coordinate,
    ): Result<Boolean> {
        return when (getLocation(currentCoordinate)) {
            null -> updateCurrentLocationFromRemote(currentCoordinate)
            else -> Result.Success(true)
        }
    }

    override suspend fun getCurrentCoordinate(): Result<Coordinate> =
        withContext(defaultDispatcher) {
            val hasAccessFineLocationPermission =
                context.checkPermission(permission.ACCESS_FINE_LOCATION)
            val hasAccessCoarseLocationPermission =
                context.checkPermission(permission.ACCESS_COARSE_LOCATION)

            val locationManager =
                context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
            val isGpsEnabled =
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER) ||
                    locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)

            if (!isGpsEnabled) return@withContext Result.Error(LocationServiceDisabledException)

            if (!hasAccessCoarseLocationPermission || !hasAccessFineLocationPermission) {
                return@withContext Result.Error(LocationPermissionDeniedException)
            }

            val lastLocation = await(client.lastLocation)
            when {
                lastLocation != null -> Result.Success(lastLocation.toCoordinate())
                else -> Result.Error(LocationException.NullLastLocation)
            }
        }

    override suspend fun getSuggestionCities(cityAddress: String): Result<List<SuggestionCity>> =
        runCatching {
            api.getForwardGeocoding(cityAddress = cityAddress).results.map { it.toSuggestionCity() }
        }

    override suspend fun getLocation(coordinate: Coordinate): LocationEntity? =
        dao.observeLocation(coordinate.latitude, coordinate.longitude).firstOrNull()

    override suspend fun getCurrentLocation() = dao.observeCurrentLocation().firstOrNull()

    override fun getLocationsStream(): Flow<List<LocationEntity>> = dao.observeLocations()

    override suspend fun saveLocation(location: LocationEntity) = dao.insertLocation(location)

    override suspend fun deleteLocation(location: LocationEntity) = dao.deleteLocation(location)

    @Suppress("TooGenericExceptionCaught")
    private suspend fun updateCurrentLocationFromRemote(coordinate: Coordinate): Result<Boolean> {
        try {
            val response = api.getReverseGeocoding(coordinate.toApiCoordinate())
            saveLocation(
                LocationEntity(
                    cityAddress = response.getCityAddress(),
                    latitude = coordinate.latitude,
                    longitude = coordinate.longitude,
                    timeZone = response.getTimeZone(),
                    isCurrentLocation = true,
                    countryCode = response.getCountryCode(),
                ),
            )
            return Result.Success(true)
        } catch (ex: Exception) {
            return Result.Error(ex)
        }
    }
}
