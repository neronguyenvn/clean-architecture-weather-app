package com.example.weatherjourney.core.network

import com.example.weatherjourney.core.model.Coordinate
import com.example.weatherjourney.core.network.model.NetworkWeather
import com.example.weatherjourney.core.network.model.ReverseGeocoding

interface WtnNetworkDataSource {

    suspend fun getWeather(coordinate: Coordinate, timeZone: String): NetworkWeather

    suspend fun getLocations(address: String): List<NetworkLocation>

    suspend fun getReverseGeocoding(coordinate: Coordinate): ReverseGeocoding
}