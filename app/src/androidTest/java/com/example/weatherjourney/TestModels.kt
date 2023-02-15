package com.example.weatherjourney

import com.example.weatherjourney.weather.data.local.entity.LocationEntity
import com.example.weatherjourney.weather.domain.model.Coordinate

const val city1 = "Thu Duc City"
val coordinate1 = Coordinate(10.873, 106.742)

val location1 = LocationEntity(city1, coordinate1.lat, coordinate1.long)
val location2 = LocationEntity("Usa", 39.784, -100.446)
