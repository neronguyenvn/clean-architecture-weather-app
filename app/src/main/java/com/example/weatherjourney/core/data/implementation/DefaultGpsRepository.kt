package com.example.weatherjourney.core.data.implementation

import android.Manifest
import android.content.Context
import android.location.LocationManager
import android.os.Looper
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
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject

private const val UPDATE_INTERVAL_MILLIS = 60000L
private const val MAX_UPDATE_DELAY_MILLIS = 60000L
private const val MIN_UPDATE_INTERVAL_MILLIS = IMPLICIT_MIN_UPDATE_INTERVAL

class DefaultGpsRepository @Inject constructor(
    private val client: FusedLocationProviderClient,
    @ApplicationContext private val context: Context,
) : GpsRepository {

    override fun getCurrentCoordinateStream(): Flow<Coordinate> = callbackFlow {
        val hasLocationPermission = when {
            context.checkPermission(Manifest.permission.ACCESS_FINE_LOCATION) -> true
            context.checkPermission(Manifest.permission.ACCESS_COARSE_LOCATION) -> true
            else -> false
        }

        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val isLocationEnabled = locationManager.isLocationEnabled

        if (!hasLocationPermission || !isLocationEnabled) {
            channel.close()
            return@callbackFlow
        }

        val locationCallback = object : LocationCallback() {
            override fun onLocationResult(result: LocationResult) {
                result.lastLocation?.let {
                    channel.trySend(it.coordinate)
                }
            }
        }

        val locationRequest = createLocationRequest()
        client.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper())

        client.lastLocation.result?.let {
            trySend(it.coordinate)
        }

        awaitClose {
            client.removeLocationUpdates(locationCallback)
        }
    }

    private fun createLocationRequest() = LocationRequest.Builder(
        Priority.PRIORITY_HIGH_ACCURACY,
        UPDATE_INTERVAL_MILLIS
    ).apply {
        setWaitForAccurateLocation(true)
        setMinUpdateIntervalMillis(MIN_UPDATE_INTERVAL_MILLIS)
        setMaxUpdateDelayMillis(MAX_UPDATE_DELAY_MILLIS)
    }.build()
}
