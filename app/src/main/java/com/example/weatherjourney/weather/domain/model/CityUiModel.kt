package com.example.weatherjourney.weather.domain.model

open class CityUiModel(
    val cityAddress: String,
    val coordinate: Coordinate,
    val timeZone: String
)

class SuggestionCity(
    val countryFlag: String,
    cityAddress: String,
    coordinate: Coordinate,
    timeZone: String
) : CityUiModel(cityAddress, coordinate, timeZone)

class SavedCity(
    val temp: Double,
    val isCurrentLocation: Boolean,
    val weatherType: WeatherType,
    cityAddress: String,
    coordinate: Coordinate,
    timeZone: String
) : CityUiModel(cityAddress, coordinate, timeZone)
