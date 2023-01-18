package com.example.weatherjourney.weather.data.source.remote

import com.example.weatherjourney.util.Result
import com.example.weatherjourney.util.runCatching
import com.example.weatherjourney.weather.data.source.LocationDataSource
import com.example.weatherjourney.weather.data.source.local.entity.LocationEntity
import com.example.weatherjourney.weather.domain.model.Coordinate

class LocationRemoteDataSource(private val apiService: ApiService) : LocationDataSource {

    override suspend fun getCoordinate(city: String): Result<Coordinate> = runCatching {
        // apiService.getForwardGeocoding(city).results.first().coordinate\

        Coordinate()
        // TODO: Fix with new autocomplete api
    }

    override suspend fun getCityName(coordinate: Coordinate): Result<String> = runCatching {
        val result =
            apiService.getReverseGeocoding("${coordinate.latitude}+${coordinate.longitude}").results.first().components
        result.getData().filter { it.isNotBlank() }.joinToString(", ")
    }

    override suspend fun saveLocation(location: LocationEntity) {
        // Not required for the remote data source
    }
}
