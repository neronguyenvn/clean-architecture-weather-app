package com.example.weatherjourney.weather.domain.mapper

import com.example.weatherjourney.locationpreferences.LocationPreferences.CoordinatePreferences
import com.example.weatherjourney.util.roundTo
import com.example.weatherjourney.weather.domain.model.Coordinate

fun CoordinatePreferences.toCoordinate(): Coordinate = Coordinate(this.latitude, this.longitude)

fun Coordinate.toCoordinatePreferences(): CoordinatePreferences =
    CoordinatePreferences.newBuilder().setLatitude(latitude).setLongitude(longitude).build()

fun Coordinate.roundTo(n: Int) = Coordinate(latitude.roundTo(n), longitude.roundTo(n))
