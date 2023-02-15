package com.example.weatherjourney.domain

import com.example.weatherjourney.weather.domain.model.Coordinate

interface PreferenceRepository {

    suspend fun getLastCoordinate(): Coordinate

    suspend fun getLastTimeZone(): String

    suspend fun getLastCityAddress(): String

    suspend fun saveCoordinate(coordinate: Coordinate)

    suspend fun saveTimeZone(timeZone: String)

    suspend fun saveCityAddress(cityAddress: String)
}
