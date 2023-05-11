package com.example.weatherjourney.features.weather.domain.usecase

import com.example.weatherjourney.features.weather.domain.usecase.location.ValidateCurrentLocation

data class LocationUseCases(
    val validateCurrentLocation: ValidateCurrentLocation,
)
