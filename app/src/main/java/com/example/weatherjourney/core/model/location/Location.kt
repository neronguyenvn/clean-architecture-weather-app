package com.example.weatherjourney.core.model.location

import com.example.weatherjourney.core.database.model.LocationEntity

data class Location(
    val cityAddress: String,
    val countryCode: String,
    val timeZone: String,
    val coordinate: Coordinate,
)

fun Location.asEntity(isDisplayed: Boolean) = LocationEntity(
    cityAddress = cityAddress,
    countryCode = countryCode,
    timeZone = timeZone,
    latitude = coordinate.latitude,
    longitude = coordinate.longitude,
    isDisplayed = isDisplayed
)