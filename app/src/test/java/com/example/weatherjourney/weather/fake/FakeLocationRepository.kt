/*
package com.example.weatherjourney.weather.fake

import com.example.weatherjourney.core.database.model.LocationEntity
import com.example.weatherjourney.features.weather.data.mapper.coordinate
import com.example.weatherjourney.core.model.location.Coordinate
import com.example.weatherjourney.core.model.location.SuggestionCity
import com.example.weatherjourney.core.data.LocationRepository
import com.example.weatherjourney.core.common.util.LocationException
import com.example.weatherjourney.core.common.util.Result
import com.example.weatherjourney.weather.coordinate1
import com.example.weatherjourney.weather.location1
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.net.UnknownHostException

class FakeLocationRepository : LocationRepository {

    private var isGpsEnabled = true
    private var hasInternet = true

    private val locations = mutableListOf<LocationEntity>()

    fun setIsGpsEnabled(isEnabled: Boolean) {
        this.isGpsEnabled = isEnabled
    }

    fun setHasInternet(hasInternet: Boolean) {
        this.hasInternet = hasInternet
    }

    override suspend fun getCurrentCoordinate(): Result<Coordinate> {
        return if (isGpsEnabled) {
            Result.Success(coordinate1)
        } else {
            Result.Error(LocationException.LocationServiceDisabledException)
        }
    }

    override suspend fun getSuggestionCities(cityAddress: String): Result<List<SuggestionCity>> {
        return Result.Success(emptyList())
    }

    override suspend fun checkAndUpdateCurrentLocationIfNeeded(currentCoordinate: Coordinate): Result<Boolean> {
        return if (hasInternet) {
            locations.add(location1)
            Result.Success(true)
        } else {
            Result.Error(UnknownHostException())
        }
    }

    override suspend fun getLocation(coordinate: Coordinate): LocationEntity? {
        return locations.find { it.coordinate == coordinate }
    }

    override suspend fun getCurrentLocation(): LocationEntity? {
        return locations.find { it.isCurrentLocation }
    }

    override fun getLocationsStream(): Flow<List<LocationEntity>> {
        return flow { emit(emptyList()) }
    }

    override suspend fun saveLocation(location: LocationEntity) {
        locations.add(location)
    }

    override suspend fun deleteLocation(location: LocationEntity) {
        // Do nothing
    }
}
*/
