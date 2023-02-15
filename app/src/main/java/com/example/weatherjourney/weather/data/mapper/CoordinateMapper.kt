package com.example.weatherjourney.weather.data.mapper

import com.example.weatherjourney.util.roundTo
import com.example.weatherjourney.weather.data.local.entity.LocationEntity
import com.example.weatherjourney.weather.domain.model.Coordinate

private const val DECIMAL_DEGREE_PRECISION = 2

fun android.location.Location.toCoordinate(): Coordinate {
    return Coordinate(lat = latitude, long = longitude)
}

/**
 * Round Coordinate to make it has united number of digits after decimal point.
 * Object that can be unambiguously recognized at this scale (2): town or village.
 */
fun Coordinate.toUnifiedCoordinate(): Coordinate {
    return Coordinate(
        lat = lat.roundTo(DECIMAL_DEGREE_PRECISION),
        long = long.roundTo(DECIMAL_DEGREE_PRECISION)
    )
}

fun Coordinate.toLocationEntity(cityAddress: String, timeZone: String): LocationEntity =
    LocationEntity(
        cityAddress = cityAddress,
        lat = lat,
        long = long,
        timeZone = timeZone
    )

fun LocationEntity.toCoordinate(): Coordinate = Coordinate(lat, long)

fun Coordinate.toApiCoordinate(): String = "$lat+$long"
