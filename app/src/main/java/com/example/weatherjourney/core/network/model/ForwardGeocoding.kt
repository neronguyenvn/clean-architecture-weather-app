package com.example.weatherjourney.core.network.model

import com.example.weatherjourney.core.model.location.Coordinate
import com.example.weatherjourney.core.model.location.SuggestionCity
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ForwardGeocoding(
    val results: List<ForwardGeocodingResult> = emptyList(),
)

@Serializable
data class ForwardGeocodingResult(
    val name: String,
    val admin1: String = "",
    @SerialName("country_code") val countryCode: String,
    val latitude: Double,
    val longitude: Double,
    val timezone: String = "",
) {
    fun getCityAddress() = when {
        name != admin1 && admin1.isNotBlank() -> "$name, $admin1"
        else -> name
    }
}


fun ForwardGeocodingResult.toSuggestionCity() = SuggestionCity(
    countryCode = countryCode,
    cityAddress = getCityAddress(),
    coordinate = Coordinate(latitude, longitude),
    timeZone = timezone,
)

