package com.example.weatherjourney.weather.domain.usecase.location

import com.example.weatherjourney.util.Result
import com.example.weatherjourney.weather.data.mapper.toUnifiedCoordinate
import com.example.weatherjourney.weather.domain.model.Coordinate
import com.example.weatherjourney.weather.domain.repository.LocationRepository

class GetCityAddressAndSaveLocation(private val repository: LocationRepository) {

    suspend operator fun invoke(coordinate: Coordinate): Result<String> =
        when (val location = repository.fetchLocation(coordinate.toUnifiedCoordinate())) {
            is Result.Success -> Result.Success(location.data.cityAddress)
            is Result.Error -> location
        }
}
