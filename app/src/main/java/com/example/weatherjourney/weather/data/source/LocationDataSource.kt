package com.example.weatherjourney.weather.data.source

import com.example.weatherjourney.util.Result
import com.example.weatherjourney.weather.data.source.local.entity.LocationEntity
import com.example.weatherjourney.weather.data.source.remote.dto.ForwardGeocoding
import com.example.weatherjourney.weather.domain.model.Coordinate

interface LocationDataSource {

    suspend fun getCityName(coordinate: Coordinate): Result<String>

    suspend fun saveLocation(location: LocationEntity)

    suspend fun fetchSuggestionLocations(city: String): Result<ForwardGeocoding>

    suspend fun getLocations(): List<LocationEntity>
}
