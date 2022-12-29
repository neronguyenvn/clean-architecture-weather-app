package com.example.weather.data

import android.annotation.SuppressLint
import android.os.Looper
import android.text.format.DateUtils
import android.util.Log
import com.example.weather.model.geocoding.Location
import com.google.android.gms.location.*
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.*

interface LocationDataSource {
    val locationSource: Flow<Location>
}

class DefaultLocationDataSource(private val client: FusedLocationProviderClient) :
    LocationDataSource {

    @SuppressLint("MissingPermission")
    override val locationSource = callbackFlow {

        val callback = object : LocationCallback() {
            override fun onLocationResult(result: LocationResult) {
                super.onLocationResult(result)

                val location = result.lastLocation
                val userLocation =
                    location?.let { Location(lat = it.latitude, lng = location.longitude) }
                userLocation?.let {
                    Log.d("LocationDataSource", userLocation.toString())
                    trySend(it)
                }
            }
        }

        client.requestLocationUpdates(locationRequest, callback, Looper.getMainLooper())
            .addOnFailureListener { e -> close(e) }
        awaitClose { client.removeLocationUpdates(callback) }
    }

    companion object {
        private const val INTERVAL_MILLIS = 15 * DateUtils.MINUTE_IN_MILLIS

        private val locationRequest = LocationRequest
            .Builder(Priority.PRIORITY_HIGH_ACCURACY, INTERVAL_MILLIS)
            .build()
    }
}