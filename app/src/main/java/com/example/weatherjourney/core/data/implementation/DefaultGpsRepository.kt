package com.example.weatherjourney.core.data.implementation

import android.Manifest
import android.content.Context
import android.location.LocationManager
import android.os.Looper
import com.example.weatherjourney.core.common.coroutine.Dispatcher
import com.example.weatherjourney.core.common.coroutine.WtnDispatchers.Default
import com.example.weatherjourney.core.common.util.checkPermission
import com.example.weatherjourney.core.data.GpsRepository
import com.example.weatherjourney.core.model.Coordinate
import com.example.weatherjourney.core.model.coordinate
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationRequest.Builder.IMPLICIT_MIN_UPDATE_INTERVAL
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.Priority
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject

class DefaultGpsRepository @Inject constructor(
    private val client: FusedLocationProviderClient,
    @ApplicationContext private val context: Context,
    @Dispatcher(Default) private val defaultDispatcher: CoroutineDispatcher
) : GpsRepository {

    override fun getCurrentCoordinateStream(): Flow<Coordinate> = callbackFlow {
        val hasAccessFineLocationPermission = context.checkPermission(
            Manifest.permission.ACCESS_FINE_LOCATION
        )
        val hasAccessCoarseLocationPermission = context.checkPermission(
            Manifest.permission.ACCESS_COARSE_LOCATION
        )

        val locationManager = context.getSystemService(
            Context.LOCATION_SERVICE
        ) as LocationManager

        val isGpsEnabled = locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        ) || locationManager.isProviderEnabled(
            LocationManager.GPS_PROVIDER
        )

        if (!isGpsEnabled) {
            channel.close()
            return@callbackFlow
        }

        if (!hasAccessCoarseLocationPermission && !hasAccessFineLocationPermission) {
            channel.close()
            return@callbackFlow
        }

        val callBack = object : LocationCallback() {
            override fun onLocationResult(result: LocationResult) {
                super.onLocationResult(result)
                result.lastLocation?.let {
                    channel.trySend(it.coordinate)
                }
            }
        }

        val request = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 60000)
            .apply {
                setWaitForAccurateLocation(false)
                setMinUpdateIntervalMillis(IMPLICIT_MIN_UPDATE_INTERVAL)
                setMaxUpdateDelayMillis(60000)
            }.build()

        client.requestLocationUpdates(request, callBack, Looper.getMainLooper())
        client.lastLocation.result?.let {
            trySend(it.coordinate)
        }

        awaitClose {
            client.removeLocationUpdates(callBack)
        }
    }
}
