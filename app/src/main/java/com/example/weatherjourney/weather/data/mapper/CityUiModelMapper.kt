package com.example.weatherjourney.weather.data.mapper

import com.example.weatherjourney.weather.data.local.entity.LocationEntity
import com.example.weatherjourney.weather.data.remote.dto.ForwardGeocodingResult
import com.example.weatherjourney.weather.domain.model.Coordinate
import com.example.weatherjourney.weather.domain.model.SavedCity
import com.example.weatherjourney.weather.domain.model.SuggestionCity
import com.example.weatherjourney.weather.domain.model.WeatherType

fun ForwardGeocodingResult.toSuggestionCity() = SuggestionCity(
    countryCode = countryCode,
    cityAddress = getCityAddress(),
    coordinate = Coordinate(latitude, longitude),
    timeZone = timezone
)

fun LocationEntity.toSavedCity(temp: Double, weatherType: WeatherType) = SavedCity(
    temp = temp,
    weatherType = weatherType,
    cityAddress = cityAddress,
    coordinate = coordinate,
    isCurrentLocation = isCurrentLocation,
    timeZone = timeZone,
    countryCode = countryCode,
    id = id
)
