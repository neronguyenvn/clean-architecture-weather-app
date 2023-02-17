package com.example.weatherjourney.weather.domain.usecase

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
