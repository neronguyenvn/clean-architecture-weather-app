package com.example.weatherjourney.weather.data.source.remote

import com.example.weatherjourney.util.Result
import com.example.weatherjourney.util.runCatching
import com.example.weatherjourney.weather.data.source.LocationDataSource
import com.example.weatherjourney.weather.data.source.local.entity.LocationEntity
import com.example.weatherjourney.weather.data.source.remote.dto.ForwardGeocoding
import com.example.weatherjourney.weather.domain.model.Coordinate

class LocationRemoteDataSource(private val apiService: ApiService) : LocationDataSource {

    override suspend fun getCityName(coordinate: Coordinate): Result<String> = runCatching {
        val result =
            apiService.getReverseGeocoding("${coordinate.latitude}+${coordinate.longitude}").results.first().components
        result.getFormattedLocationString()
    }

    override suspend fun fetchSuggestionLocations(city: String): Result<ForwardGeocoding> =
        runCatching {
            apiService.getForwardGeocoding(name = city)
        }

    override suspend fun saveLocation(location: LocationEntity) {
        throw Exception("Not required for the remote data source")
    }
}
