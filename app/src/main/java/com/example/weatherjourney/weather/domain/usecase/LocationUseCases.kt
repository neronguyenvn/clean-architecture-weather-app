package com.example.weatherjourney.weather.domain.usecase

import com.example.weatherjourney.weather.domain.usecase.location.DeleteLocation
import com.example.weatherjourney.weather.domain.usecase.location.GetCityAddressAndSaveLocation
import com.example.weatherjourney.weather.domain.usecase.location.GetCurrentCoordinate
import com.example.weatherjourney.weather.domain.usecase.location.GetLocationsStream
import com.example.weatherjourney.weather.domain.usecase.location.GetSuggestionCities
import com.example.weatherjourney.weather.domain.usecase.location.SaveLocation
import com.example.weatherjourney.weather.domain.usecase.location.ShouldSaveLocation
import com.example.weatherjourney.weather.domain.usecase.location.ValidateCurrentCoordinate
import com.example.weatherjourney.weather.domain.usecase.location.ValidateCurrentLocation
import com.example.weatherjourney.weather.domain.usecase.location.ValidateLastInfo

data class LocationUseCases(
    val saveLocation: SaveLocation,
    val shouldSaveLocation: ShouldSaveLocation,
    val getCurrentCoordinate: GetCurrentCoordinate,
    val validateCurrentCoordinate: ValidateCurrentCoordinate,
    val validateLastInfo: ValidateLastInfo,
    val getCityAddressAndSaveLocation: GetCityAddressAndSaveLocation,
    val getSuggestionCities: GetSuggestionCities,
    val getLocationsStream: GetLocationsStream,
    val deleteLocation: DeleteLocation,
    val validateCurrentLocation: ValidateCurrentLocation
)
