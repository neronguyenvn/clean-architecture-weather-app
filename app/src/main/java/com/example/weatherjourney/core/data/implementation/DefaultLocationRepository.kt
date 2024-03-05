package com.example.weatherjourney.core.data.implementation

import android.Manifest.permission
import android.content.Context
import android.location.LocationManager
import com.example.weatherjourney.core.common.coroutine.Dispatcher
import com.example.weatherjourney.core.common.coroutine.WtnDispatchers.Default
import com.example.weatherjourney.core.common.util.LocationException
import com.example.weatherjourney.core.common.util.LocationException.LocationPermissionDeniedException
import com.example.weatherjourney.core.common.util.LocationException.LocationServiceDisabledException
import com.example.weatherjourney.core.common.util.Result
import com.example.weatherjourney.core.common.util.checkPermission
import com.example.weatherjourney.core.common.util.runCatching
import com.example.weatherjourney.core.data.LocationRepository
import com.example.weatherjourney.core.database.LocationDao
import com.example.weatherjourney.core.database.model.LocationEntity
import com.example.weatherjourney.core.model.location.Coordinate
import com.example.weatherjourney.core.model.location.SuggestionCity
import com.example.weatherjourney.core.model.location.toCoordinate
import com.example.weatherjourney.core.network.WtnNetworkDataSource
import com.example.weatherjourney.core.network.model.toSuggestionCity
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.tasks.Tasks.await
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.withContext
import javax.inject.Inject

class DefaultLocationRepository @Inject constructor(
    private val locationDao: LocationDao,
    private val network: WtnNetworkDataSource,
    private val client: FusedLocationProviderClient,

    @ApplicationContext
    private val context: Context,

    @Dispatcher(Default)
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
            network.getForwardGeocoding(cityAddress = cityAddress).results.map { it.toSuggestionCity() }
        }

    override suspend fun getLocation(coordinate: Coordinate): LocationEntity? =
        locationDao.observeLocation(coordinate.latitude, coordinate.longitude).firstOrNull()

    override suspend fun getCurrentLocation() = locationDao.observeCurrentLocation().firstOrNull()

    override fun getLocationsStream(): Flow<List<LocationEntity>> = locationDao.observeLocations()

    override suspend fun saveLocation(location: LocationEntity) =
        locationDao.insertLocation(location)

    override suspend fun deleteLocation(location: LocationEntity) =
        locationDao.deleteLocation(location)

    @Suppress("TooGenericExceptionCaught")
    private suspend fun updateCurrentLocationFromRemote(coordinate: Coordinate): Result<Boolean> {
        try {
            val response = network.getReverseGeocoding(coordinate)
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
