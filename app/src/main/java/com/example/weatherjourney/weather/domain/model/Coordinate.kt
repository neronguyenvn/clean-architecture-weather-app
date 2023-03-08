package com.example.weatherjourney.weather.domain.model

import com.example.weatherjourney.util.roundTo
import java.util.Objects

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

    override fun toString(): String = "Coordinate(latitude=$latitude, longitude=$longitude)"

    override fun equals(other: Any?): Boolean {
        if (this === other) return true // check if identical
        if (other !is Coordinate) return false // check if same class and cast

        if (latitude != other.latitude) return false
        if (longitude != other.longitude) return false

        return true // all properties are equal
    }

    override fun hashCode(): Int {
        return Objects.hash(latitude, longitude)
    }
}
