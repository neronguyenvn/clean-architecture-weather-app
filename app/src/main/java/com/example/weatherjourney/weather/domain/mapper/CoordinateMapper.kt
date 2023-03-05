package com.example.weatherjourney.weather.domain.mapper

import com.example.weatherejourney.LocationPreferences.CoordinatePreferences
import com.example.weatherjourney.weather.domain.model.Coordinate

fun CoordinatePreferences.toCoordinate(): Coordinate = Coordinate(this.latitude, this.longitude)

fun Coordinate.toCoordinatePreferences(): CoordinatePreferences =
    CoordinatePreferences.newBuilder().setLatitude(latitude).setLongitude(longitude).build()
