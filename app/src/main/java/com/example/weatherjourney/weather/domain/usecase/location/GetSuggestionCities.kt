package com.example.weatherjourney.weather.domain.usecase.location

import com.example.weatherjourney.util.Result
import com.example.weatherjourney.weather.domain.model.SuggestionCity
import com.example.weatherjourney.weather.domain.repository.LocationRepository

class GetSuggestionCities(private val repository: LocationRepository) {

    suspend operator fun invoke(cityAddress: String): Result<List<SuggestionCity>> =
        repository.getSuggestionLocations(cityAddress)
}
