package com.example.weatherjourney.weather.domain.usecase.location

import com.example.weatherjourney.weather.domain.model.Coordinate

class ValidateLocation {

    operator fun invoke(cityAddress: String, coordinate: Coordinate, timeZone: String) =
        cityAddress.isNotBlank() && coordinate.latitude != 0.0 && coordinate.longitude != 0.0 && timeZone.isNotBlank()
}
