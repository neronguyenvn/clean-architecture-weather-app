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
import com.example.weatherjourney.core.database.model.LocationWithWeather
import com.example.weatherjourney.core.model.location.Coordinate
import com.example.weatherjourney.core.model.location.Location
import com.example.weatherjourney.core.model.location.SuggestionCity
import com.example.weatherjourney.core.model.location.asEntity
import com.example.weatherjourney.core.model.location.coordinate
import com.example.weatherjourney.core.network.WtnNetworkDataSource
import com.example.weatherjourney.core.network.model.toSuggestionCity
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.tasks.Tasks.await
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
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

    override fun getDisplayedLocationStream(): Flow<LocationEntity?> {
        return locationDao.observeDisplayed()
    }

    override fun getDisplayedLocationWithWeatherStream(): Flow<LocationWithWeather?> {
        return locationDao.observeDisplayedWithWeather()
    }

    override fun getAllLocationWithWeatherStream(): Flow<List<LocationWithWeather>> {
        return locationDao.observeAllWithWeather()
    }

    override suspend fun saveLocation(location: Location, isVisible: Boolean) {
        locationDao.insert(location.asEntity(isVisible))
    }

    override suspend fun deleteLocation(locationId: Int) {
        locationDao.deleteById(locationId)
    }

    override suspend fun getCurrentCoordinate(): Result<Coordinate> =
        withContext(defaultDispatcher) {
            val hasAccessFineLocationPermission =
                context.checkPermission(permission.ACCESS_FINE_LOCATION)
            val hasAccessCoarseLocationPermission =
                context.checkPermission(permission.ACCESS_COARSE_LOCATION)

            val locationManager = context.getSystemService(Context.LOCATION_SERVICE)
                    as LocationManager
            val isGpsEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
                    || locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)

            if (!isGpsEnabled) return@withContext Result.Error(LocationServiceDisabledException)

            if (!hasAccessCoarseLocationPermission || !hasAccessFineLocationPermission) {
                return@withContext Result.Error(LocationPermissionDeniedException)
            }

            val lastLocation = await(client.lastLocation)
            when {
                lastLocation != null -> Result.Success(lastLocation.coordinate)
                else -> Result.Error(LocationException.NullLastLocation)
            }
        }

    override suspend fun getSuggestionCities(cityAddress: String): Result<List<SuggestionCity>> =
        runCatching {
            network.getForwardGeocoding(cityAddress = cityAddress).results.map { it.toSuggestionCity() }
        }
}
