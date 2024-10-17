package com.example.weatherjourney.core.model.search

import com.example.weatherjourney.core.database.model.LocationEntity
import com.example.weatherjourney.core.model.Coordinate

data class Location(
    val id: Int,
    val address: String,
    val countryCode: String,
    val timeZone: String,
    val coordinate: Coordinate,
)

fun Location.asEntity() = LocationEntity(
    address = address,
    countryCode = countryCode,
    timeZone = timeZone,
    latitude = coordinate.lat,
    longitude = coordinate.long,
)
