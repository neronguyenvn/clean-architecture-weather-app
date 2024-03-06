package com.example.weatherjourney.core.data

import com.example.weatherjourney.core.common.util.Result
import com.example.weatherjourney.core.database.model.LocationEntity
import com.example.weatherjourney.core.database.model.LocationWithWeather
import com.example.weatherjourney.core.model.location.Coordinate
import com.example.weatherjourney.core.model.location.Location
import com.example.weatherjourney.core.model.location.SuggestionCity
import kotlinx.coroutines.flow.Flow

interface LocationRepository {

    fun getDisplayedLocationStream(): Flow<LocationEntity?>

    fun getDisplayedLocationWithWeatherStream(): Flow<LocationWithWeather?>

    fun getAllLocationWithWeatherStream(): Flow<List<LocationWithWeather>>

    fun getCurrentCoordinateStream(): Flow<Result<Coordinate>>

    suspend fun saveLocation(location: Location, isVisible: Boolean)

    suspend fun deleteLocation(locationId: Int)

    suspend fun getSuggestionCities(cityAddress: String): Result<List<SuggestionCity>>
}
