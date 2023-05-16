package com.example.weatherjourney.features.weather.data.mapper

import com.example.weatherjourney.features.weather.data.local.entity.LocationEntity
import com.example.weatherjourney.features.weather.domain.model.Coordinate

fun android.location.Location.toCoordinate(): Coordinate {
    return Coordinate(latitude = latitude, longitude = longitude)
}

val LocationEntity.coordinate: Coordinate
    get() = Coordinate(latitude, longitude)

fun Coordinate.toApiCoordinate(): String = "$latitude+$longitude"
