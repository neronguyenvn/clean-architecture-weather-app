package com.example.weatherjourney.features.weather.domain.model

import java.util.Objects

open class CityUiModel(
    val cityAddress: String,
    val coordinate: Coordinate,
    val timeZone: String,
    val countryCode: String,
) {
    override fun toString(): String = "CityUiModel(cityAddress=$cityAddress)"

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is CityUiModel) return false

        if (coordinate != other.coordinate) return false

        return true
    }

    override fun hashCode() = Objects.hash(cityAddress, coordinate, timeZone, countryCode)
}

class SuggestionCity(
    cityAddress: String,
    coordinate: Coordinate,
    timeZone: String,
    countryCode: String,
) : CityUiModel(cityAddress, coordinate, timeZone, countryCode)

class SavedCity(
    val temp: Double,
    val isCurrentLocation: Boolean,
    val weatherType: WeatherType,
    val id: Int,
    cityAddress: String,
    coordinate: Coordinate,
    timeZone: String,
    countryCode: String,
) : CityUiModel(cityAddress, coordinate, timeZone, countryCode) {

    fun copy(
        temp: Double = this.temp,
        isCurrentLocation: Boolean = this.isCurrentLocation,
        weatherType: WeatherType = this.weatherType,
        cityAddress: String = this.cityAddress,
        coordinate: Coordinate = this.coordinate,
        timeZone: String = this.timeZone,
        countryCode: String = this.countryCode,
        id: Int = this.id,
    ): SavedCity = SavedCity(
        temp = temp,
        isCurrentLocation = isCurrentLocation,
        weatherType = weatherType,
        cityAddress = cityAddress,
        coordinate = coordinate,
        timeZone = timeZone,
        countryCode = countryCode,
        id = id,
    )
}
