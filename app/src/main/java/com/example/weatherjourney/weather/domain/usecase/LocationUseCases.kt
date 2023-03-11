package com.example.weatherjourney.weather.domain.usecase

import com.example.weatherjourney.weather.domain.usecase.location.DeleteLocation
import com.example.weatherjourney.weather.domain.usecase.location.GetAndSaveCurrentLocation
import com.example.weatherjourney.weather.domain.usecase.location.GetCurrentCoordinate
import com.example.weatherjourney.weather.domain.usecase.location.GetLocationsStream
import com.example.weatherjourney.weather.domain.usecase.location.GetSuggestionCities
import com.example.weatherjourney.weather.domain.usecase.location.IsCurrentLocation
import com.example.weatherjourney.weather.domain.usecase.location.SaveLocation
import com.example.weatherjourney.weather.domain.usecase.location.ShouldSaveLocation
import com.example.weatherjourney.weather.domain.usecase.location.ValidateCurrentLocation

data class LocationUseCases(
    val saveLocation: SaveLocation,
    val shouldSaveLocation: ShouldSaveLocation,
    val getCurrentCoordinate: GetCurrentCoordinate,
    val validateCurrentLocation: ValidateCurrentLocation,
    val getAndSaveCurrentLocation: GetAndSaveCurrentLocation,
    val getSuggestionCities: GetSuggestionCities,
    val getLocationsStream: GetLocationsStream,
    val deleteLocation: DeleteLocation,
    val isCurrentLocation: IsCurrentLocation
)
