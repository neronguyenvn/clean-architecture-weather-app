package com.example.weatherjourney.weather.domain.model

import com.example.weatherjourney.util.roundTo

// Object that can be unambiguously recognized at this scale (2): town or village.
private const val DECIMAL_DEGREE_PRECISION = 2

/**
 * Round Coordinate to make it has united number of digits after decimal point.
 */
class Coordinate(latitude: Double, longitude: Double) {

    val latitude: Double = latitude
        get() = if (field != 0.0) field.roundTo(DECIMAL_DEGREE_PRECISION) else 0.0

    val longitude: Double = longitude
        get() = if (field != 0.0) field.roundTo(DECIMAL_DEGREE_PRECISION) else 0.0
}
