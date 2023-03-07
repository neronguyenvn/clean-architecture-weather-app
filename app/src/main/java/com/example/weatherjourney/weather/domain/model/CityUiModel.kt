package com.example.weatherjourney.weather.domain.model

open class CityUiModel(
    val cityAddress: String,
    val coordinate: Coordinate,
    val timeZone: String,
    val countryCode: String
) {
    override fun toString(): String =
        "CityUiModel(cityAddress=$cityAddress, coordinate=$coordinate, timeZone=$timeZone)"
}

class SuggestionCity(
    cityAddress: String,
    coordinate: Coordinate,
    timeZone: String,
    countryCode: String
) : CityUiModel(cityAddress, coordinate, timeZone, countryCode)

class SavedCity(
    val temp: Double,
    val isCurrentLocation: Boolean,
    val weatherType: WeatherType,
    cityAddress: String,
    coordinate: Coordinate,
    timeZone: String,
    countryCode: String
) : CityUiModel(cityAddress, coordinate, timeZone, countryCode)
