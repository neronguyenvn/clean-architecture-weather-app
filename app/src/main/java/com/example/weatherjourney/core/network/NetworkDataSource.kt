package com.example.weatherjourney.core.network

import com.example.weatherjourney.core.model.Coordinate
import com.example.weatherjourney.core.network.model.NetworkLocation
import com.example.weatherjourney.core.network.model.NetworkWeather
import com.example.weatherjourney.core.network.model.ReversedNetworkLocation

interface NetworkDataSource {

    suspend fun getWeather(coordinate: Coordinate, timeZone: String): NetworkWeather

    suspend fun searchLocationsByAddress(address: String): List<NetworkLocation>

    suspend fun getReverseGeocoding(coordinate: Coordinate): ReversedNetworkLocation
}
