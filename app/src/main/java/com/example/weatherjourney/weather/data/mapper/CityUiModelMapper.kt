package com.example.weatherjourney.weather.data.mapper

import com.example.weatherjourney.util.toFlagEmoji
import com.example.weatherjourney.weather.data.remote.dto.AllWeatherDto
import com.example.weatherjourney.weather.data.remote.dto.ForwardGeocodingResult
import com.example.weatherjourney.weather.domain.model.Coordinate
import com.example.weatherjourney.weather.domain.model.SavedCity
import com.example.weatherjourney.weather.domain.model.SuggestionCity
import com.example.weatherjourney.weather.domain.model.WeatherType

fun ForwardGeocodingResult.toSuggestionCity() = SuggestionCity(
    countryFlag = countryCode.toFlagEmoji(),
    cityAddress = getCityAddress(),
    coordinate = Coordinate(latitude, longitude),
    timeZone = timezone
)

fun AllWeatherDto.toSavedCity(
    cityAddress: String,
    coordinate: Coordinate,
    timeZone: String,
    isCurrentLocation: Boolean
): SavedCity {
    this.hourly.apply {
        return SavedCity(
            temp = temperatures[0],
            weatherType = WeatherType.fromWMO(weatherCodes[0]),
            cityAddress = cityAddress,
            coordinate = coordinate,
            isCurrentLocation = isCurrentLocation,
            timeZone = timeZone
        )
    }
}
