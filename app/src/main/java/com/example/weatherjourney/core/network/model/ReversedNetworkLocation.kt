package com.example.weatherjourney.core.network.model

import com.example.weatherjourney.core.database.model.LocationEntity
import com.example.weatherjourney.core.model.Coordinate
import kotlinx.serialization.Serializable

@Serializable
data class ReversedNetworkLocation(
    val countryCode: String,
    val city: String,
    val locality: String,
    val localityInfo: LocalityInfo
) {
    fun getAddress() = "$locality, $city"
    fun getTimezone() = localityInfo.informative
        .find { it.description == "time zone" }?.name ?: ""
}

@Serializable
data class LocalityInfo(
    val informative: List<InfoSample>
)

@Serializable
data class InfoSample(
    val name: String,
    val description: String,
)


fun ReversedNetworkLocation.asEntity(coordinate: Coordinate) = LocationEntity(
    address = getAddress(),
    countryCode = countryCode,
    timeZone = getTimezone(),
    latitude = coordinate.lat,
    longitude = coordinate.long,
)
