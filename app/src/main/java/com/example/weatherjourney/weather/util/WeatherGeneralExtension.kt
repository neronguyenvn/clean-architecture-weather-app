package com.example.weatherjourney.weather.util

import com.example.weatherjourney.weather.domain.model.Coordinate

fun Coordinate.isValid(): Boolean = latitude != 0.0 && longitude != 0.0
