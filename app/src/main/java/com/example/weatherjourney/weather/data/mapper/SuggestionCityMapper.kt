package com.example.weatherjourney.weather.data.mapper

import com.example.weatherjourney.util.toFlagEmoji
import com.example.weatherjourney.weather.data.source.remote.dto.ForwardGeocodingResult
import com.example.weatherjourney.weather.domain.model.Coordinate
import com.example.weatherjourney.weather.domain.model.SuggestionCity

fun ForwardGeocodingResult.toSuggestionCity() = SuggestionCity(
    countryFlag = countryCode.toFlagEmoji(),
    location = listOf(name, admin2, admin1, country).filter { it.isNotBlank() }
        .joinToString(", "),
    coordinate = Coordinate(latitude, longitude)
)
