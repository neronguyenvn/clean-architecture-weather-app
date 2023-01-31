package com.example.weatherjourney.weather.data.mapper

import com.example.weatherjourney.util.roundTo
import com.example.weatherjourney.weather.data.source.local.entity.LocationEntity
import com.example.weatherjourney.weather.domain.model.Coordinate

private const val DECIMAL_DEGREE_PRECISION = 2

fun android.location.Location.toCoordinate(): Coordinate {
    return Coordinate(latitude = latitude, longitude = longitude)
}

/**
 * Round Coordinate to make it has united number of digits after decimal point.
 * Object that can be unambiguously recognized at this scale (2): town or village.
 */
fun Coordinate.toUnifiedCoordinate(): Coordinate {
    return Coordinate(
        latitude = latitude.roundTo(DECIMAL_DEGREE_PRECISION),
        longitude = longitude.roundTo(DECIMAL_DEGREE_PRECISION)
    )
}

fun Coordinate.toLocation(city: String): LocationEntity =
    LocationEntity(city = city, latitude = latitude, longitude = longitude)

fun LocationEntity.toCoordinate(): Coordinate = Coordinate(latitude, longitude)
