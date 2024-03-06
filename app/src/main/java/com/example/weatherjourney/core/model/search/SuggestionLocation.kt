package com.example.weatherjourney.core.model.search

import com.example.weatherjourney.core.database.model.LocationEntity
import com.example.weatherjourney.core.model.Coordinate

data class SuggestionLocation(
    val address: String,
    val countryCode: String,
    val timeZone: String,
    val coordinate: Coordinate,
)

fun SuggestionLocation.asEntity(isDisplayed: Boolean) = LocationEntity(
    address = address,
    countryCode = countryCode,
    timeZone = timeZone,
    latitude = coordinate.latitude,
    longitude = coordinate.longitude,
    isDisplayed = isDisplayed
)