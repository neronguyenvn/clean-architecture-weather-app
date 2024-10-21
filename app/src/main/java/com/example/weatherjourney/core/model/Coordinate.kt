package com.example.weatherjourney.core.model

import com.example.weatherjourney.core.common.util.roundTo

@ConsistentCopyVisibility
data class Coordinate private constructor(
    val lat: Double,
    val long: Double
) {
    init {
        require(lat in -90.0..90.0) {
            "Latitude must be in range -90.0..90.0"
        }
        require(long in -180.0..180.0) {
            "Longitude must be in range -180.0..180.0"
        }
    }

    private constructor(
        lat: Double,
        long: Double,
        precision: Int = INITIALIZATION_PRECISION
    ) : this(
        lat.roundTo(precision), long.roundTo(precision)
    )

    companion object {
        private const val INITIALIZATION_PRECISION = 2

        fun create(
            lat: Double,
            long: Double,
            precision: Int = INITIALIZATION_PRECISION
        ): Coordinate {
            return Coordinate(lat, long, precision)
        }
    }
}

val android.location.Location.coordinate
    get() = Coordinate.create(latitude, longitude)
