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
) : CityUiModel(cityAddress, coordinate, timeZone, countryCode) {
    override fun toString(): String {
        return ("${super.toString()}(temp=$temp)")
    }

    fun copy(
        temp: Double = this.temp,
        isCurrentLocation: Boolean = this.isCurrentLocation,
        weatherType: WeatherType = this.weatherType,
        cityAddress: String = this.cityAddress,
        coordinate: Coordinate = this.coordinate,
        timeZone: String = this.timeZone,
        countryCode: String = this.countryCode
    ): SavedCity = SavedCity(
        temp,
        isCurrentLocation,
        weatherType,
        cityAddress,
        coordinate,
        timeZone,
        countryCode
    )
}
