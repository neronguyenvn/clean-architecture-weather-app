package com.example.weatherjourney.weather.domain.model

open class CityUiModel(
    val location: String,
    val coordinate: Coordinate
)

class SuggestionCity(
    val countryFlag: String,
    location: String,
    coordinate: Coordinate
) : CityUiModel(location, coordinate)

class SavedCity(
    val weather: String,
    val temp: Int,
    val imageUrl: String,
    location: String,
    coordinate: Coordinate
) : CityUiModel(location, coordinate)
