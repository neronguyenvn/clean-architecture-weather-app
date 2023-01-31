package com.example.weatherjourney.weather.data.source.local

import com.example.weatherjourney.util.Result
import com.example.weatherjourney.util.runCatching
import com.example.weatherjourney.weather.data.source.LocationDataSource
import com.example.weatherjourney.weather.data.source.local.entity.LocationEntity
import com.example.weatherjourney.weather.data.source.remote.dto.ForwardGeocoding
import com.example.weatherjourney.weather.domain.model.Coordinate
import kotlinx.coroutines.flow.first

class LocationLocalDataSource(private val dao: LocationDao) : LocationDataSource {

    override suspend fun getCityName(coordinate: Coordinate): Result<String> = runCatching {
        dao.getLocationByCoordinate(coordinate.latitude, coordinate.longitude)
            .first().city
    }

    override suspend fun saveLocation(location: LocationEntity) {
        dao.insert(location)
    }

    override suspend fun getLocations(): List<LocationEntity> = dao.getLocations().first()

    override suspend fun fetchSuggestionLocations(city: String): Result<ForwardGeocoding> {
        throw Exception("Not required for the remote data source")
    }
}
