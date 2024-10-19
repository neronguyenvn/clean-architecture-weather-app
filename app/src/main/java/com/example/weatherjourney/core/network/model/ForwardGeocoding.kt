package com.example.weatherjourney.core.network.model

import com.example.weatherjourney.core.model.Coordinate
import com.example.weatherjourney.core.model.Location
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ForwardGeocoding(
    val results: List<NetworkLocation> = emptyList(),
)

@Serializable
data class NetworkLocation(
    val name: String,
    val admin1: String,
    val latitude: Double,
    val longitude: Double,
    val timezone: String,

    @SerialName("country_code")
    val countryCode: String,
) {
    val address
        get() = when {
            name != admin1 && admin1.isNotBlank() -> "$name, $admin1"
            else -> name
        }

    val coordinate get() = Coordinate.create(latitude, longitude)
}

fun NetworkLocation.asExternalModel() = Location(
    countryCode = countryCode,
    address = address,
    coordinate = coordinate,
    timeZone = timezone,
    id = hashCode()
)

