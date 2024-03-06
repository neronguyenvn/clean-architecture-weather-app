package com.example.weatherjourney.core.data.implementation

import android.Manifest
import android.content.Context
import android.location.LocationManager
import com.example.weatherjourney.core.common.coroutine.Dispatcher
import com.example.weatherjourney.core.common.coroutine.WtnDispatchers.Default
import com.example.weatherjourney.core.common.util.LocationException
import com.example.weatherjourney.core.common.util.Result
import com.example.weatherjourney.core.common.util.checkPermission
import com.example.weatherjourney.core.data.GpsRepository
import com.example.weatherjourney.core.model.Coordinate
import com.example.weatherjourney.core.model.coordinate
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.tasks.Tasks
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import javax.inject.Inject

class DefaultGpsRepository @Inject constructor(
    private val client: FusedLocationProviderClient,

    @ApplicationContext
    private val context: Context,

    @Dispatcher(Default)
    private val defaultDispatcher: CoroutineDispatcher
) : GpsRepository {
    override fun getCurrentCoordinateStream(): Flow<Result<Coordinate>> =
        flow {
            val hasAccessFineLocationPermission =
                context.checkPermission(Manifest.permission.ACCESS_FINE_LOCATION)
            val hasAccessCoarseLocationPermission =
                context.checkPermission(Manifest.permission.ACCESS_COARSE_LOCATION)

            val locationManager = context.getSystemService(Context.LOCATION_SERVICE)
                    as LocationManager
            val isGpsEnabled =
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
                        || locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)

            if (!isGpsEnabled) {
                emit(Result.Error(LocationException.LocationServiceDisabledException))
                return@flow
            }

            if (!hasAccessCoarseLocationPermission || !hasAccessFineLocationPermission) {
                emit(Result.Error(LocationException.LocationPermissionDeniedException))
                return@flow
            }

            val lastLocation = withContext(defaultDispatcher) {
                Tasks.await(client.lastLocation)
            }
            emit(
                when {
                    lastLocation != null -> Result.Success(lastLocation.coordinate)
                    else -> Result.Error(LocationException.NullLastLocation)
                }
            )
        }
}