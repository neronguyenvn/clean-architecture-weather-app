package com.example.weather.data

import com.example.weather.di.ApplicationScope
import com.example.weather.model.geocoding.Location
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted.Companion.WhileSubscribed
import kotlinx.coroutines.flow.shareIn

interface LocationRepository {
    val location: Flow<Location>
}

class DefaultLocationRepository(
    dataSource: LocationDataSource,
    @ApplicationScope externalScope: CoroutineScope
) : LocationRepository {

    override val location: Flow<Location> = dataSource.locationSource.shareIn(
        externalScope,
        WhileSubscribed(5000)
    )
}
