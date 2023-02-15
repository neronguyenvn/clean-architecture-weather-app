package com.example.weatherjourney.weather.domain.usecase

import com.example.weatherjourney.weather.domain.model.Coordinate

class ValidateLastInfo {

    operator fun invoke(coordinate: Coordinate, timeZone: String, cityAddress: String): Boolean =
        coordinate.lat != 0.0 && coordinate.long != 0.0 && timeZone.isNotBlank() && cityAddress.isNotBlank()
}
