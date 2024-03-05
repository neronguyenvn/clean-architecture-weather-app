package com.example.weatherjourney.core.network

import com.example.weatherjourney.core.model.location.Coordinate
import com.example.weatherjourney.core.network.model.ForwardGeocoding
import com.example.weatherjourney.core.network.model.NetworkAllWeather
import com.example.weatherjourney.core.network.model.ReverseGeocoding

interface WtnNetworkDataSource {

    suspend fun getAllWeather(coordinate: Coordinate, timeZone: String): NetworkAllWeather

    suspend fun getForwardGeocoding(cityAddress: String): ForwardGeocoding

    suspend fun getReverseGeocoding(coordinate: Coordinate): ReverseGeocoding
}