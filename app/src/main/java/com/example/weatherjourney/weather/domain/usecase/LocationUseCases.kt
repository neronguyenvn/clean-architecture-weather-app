package com.example.weatherjourney.weather.domain.usecase

data class LocationUseCases(
    val saveLocation: SaveLocation,
    val shouldSaveLocation: ShouldSaveLocation,
    val getCurrentCoordinate: GetCurrentCoordinate,
    val validateLastInfo: ValidateLastInfo,
    val getCityAddress: GetCityAddress,
    val getSuggestionCities: GetSuggestionCities,
    val getLocations: GetLocations,
    val deleteLocation: DeleteLocation
)
