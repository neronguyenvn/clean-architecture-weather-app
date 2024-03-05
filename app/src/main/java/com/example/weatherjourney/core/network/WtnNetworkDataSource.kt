package com.example.weatherjourney.core.network

import com.example.weatherjourney.core.model.location.Coordinate
import com.example.weatherjourney.core.network.model.ForwardGeocoding
import com.example.weatherjourney.core.network.model.NetworkWeather
import com.example.weatherjourney.core.network.model.ReverseGeocoding

interface WtnNetworkDataSource {

    suspend fun getWeather(coordinate: Coordinate, timeZone: String): NetworkWeather

    suspend fun getForwardGeocoding(cityAddress: String): ForwardGeocoding

    suspend fun getReverseGeocoding(coordinate: Coordinate): ReverseGeocoding
}