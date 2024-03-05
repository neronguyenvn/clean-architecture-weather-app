package com.example.weatherjourney.core.model.location

import com.example.weatherjourney.core.common.util.roundTo
import com.example.weatherjourney.core.model.serializer.CoordinateSerializer
import kotlinx.serialization.Serializable
import java.util.Objects

// Object that can be unambiguously recognized at this scale (2): town or village.
private const val DECIMAL_DEGREE_PRECISION = 2

/**
 * Round Coordinate to make it has united number of digits after decimal point.
 */
@Serializable(with = CoordinateSerializer::class)
class Coordinate(latitude: Float, longitude: Float) {

    val latitude: Float = latitude
        get() = field.roundTo(DECIMAL_DEGREE_PRECISION)

    val longitude: Float = longitude
        get() = field.roundTo(DECIMAL_DEGREE_PRECISION)

    constructor() : this(0f, 0f)

    override fun toString(): String = "Coordinate(latitude=$latitude, longitude=$longitude)"

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Coordinate) return false

        if (latitude != other.latitude) return false
        if (longitude != other.longitude) return false

        return true
    }

    override fun hashCode(): Int {
        return Objects.hash(latitude, longitude)
    }
}

fun Coordinate.roundTo(n: Int) = Coordinate(latitude.roundTo(n), longitude.roundTo(n))

fun Coordinate.toApiCoordinate(): String = "$latitude+$longitude"

val android.location.Location.coordinate
    get() = Coordinate(latitude = latitude.toFloat(), longitude = longitude.toFloat())



