package com.example.weatherjourney.core.network.model

import com.example.weatherjourney.core.model.Coordinate
import com.example.weatherjourney.core.model.search.Location
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
    val latitude: Float,
    val longitude: Float,
    val timezone: String = "",
) {
    fun getAddress() = when {
        name != admin1 && admin1.isNotBlank() -> "$name, $admin1"
        else -> name
    }
}


fun ForwardGeocodingResult.toSuggestionLocation() = Location(
    countryCode = countryCode,
    address = getAddress(),
    coordinate = Coordinate(latitude, longitude),
    timeZone = timezone,
)

