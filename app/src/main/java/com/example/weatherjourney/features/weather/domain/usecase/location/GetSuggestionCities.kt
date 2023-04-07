package com.example.weatherjourney.features.weather.domain.usecase.location

import com.example.weatherjourney.features.weather.domain.model.SuggestionCity
import com.example.weatherjourney.features.weather.domain.repository.LocationRepository
import com.example.weatherjourney.util.Result

class GetSuggestionCities(private val repository: LocationRepository) {

    suspend operator fun invoke(cityAddress: String): Result<List<SuggestionCity>> =
        repository.getSuggestionLocations(cityAddress)
}
