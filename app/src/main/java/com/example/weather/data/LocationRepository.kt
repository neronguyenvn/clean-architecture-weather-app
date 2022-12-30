package com.example.weather.data

import android.annotation.SuppressLint
import com.example.weather.di.DefaultDispatcher
import com.example.weather.model.geocoding.Location
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.Tasks.await
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

/**
 * Interface for Repository of Location DataType
 */
interface LocationRepository {
    // Get the current location of the device
    suspend fun getCurrentLocation(): Location
}

/**
 * Implementation of Interface for Repository of Location DataType
 */
class DefaultLocationRepository(
    private val client: FusedLocationProviderClient,
    @DefaultDispatcher private val dispatcher: CoroutineDispatcher
) : LocationRepository {

    @SuppressLint("MissingPermission")
    override suspend fun getCurrentLocation(): Location = withContext(dispatcher) {
        val locationTask = client.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null)
        val sysLocation = await(locationTask)
        Location(latitude = sysLocation.latitude, longitude = sysLocation.longitude)
    }
}
