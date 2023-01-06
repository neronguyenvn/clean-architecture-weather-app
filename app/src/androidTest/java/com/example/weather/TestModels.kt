package com.example.weather

import com.example.weather.model.database.Location
import com.example.weather.model.geocoding.Coordinate

const val city1 = "Thu Duc City"
val coordinate1 = Coordinate(10.873, 106.742)

val location1 = Location(city1, coordinate1.latitude, coordinate1.longitude)
val location2 = Location("Usa", 39.784, -100.446)
