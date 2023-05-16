package com.example.weatherjourney.features.weather.domain.mapper

import com.example.weatherjourney.features.weather.domain.model.Coordinate
import com.example.weatherjourney.locationpreferences.LocationPreferences
import com.example.weatherjourney.util.roundTo

val LocationPreferences.coordinate: Coordinate
    get() = Coordinate(this.latitude, this.longitude)

fun Coordinate.roundTo(n: Int) = Coordinate(latitude.roundTo(n), longitude.roundTo(n))
