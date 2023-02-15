package com.example.weatherjourney.weather.data.mapper

import com.example.weatherjourney.util.toFlagEmoji
import com.example.weatherjourney.weather.data.remote.dto.ForwardGeocodingResult
import com.example.weatherjourney.weather.domain.model.Coordinate
import com.example.weatherjourney.weather.domain.model.SuggestionCity

fun ForwardGeocodingResult.toSuggestionCity() = SuggestionCity(
    countryFlag = countryCode.toFlagEmoji(),
    cityAddress = getCityAddress(),
    coordinate = Coordinate(latitude, longitude),
    timeZone = timezone
)
