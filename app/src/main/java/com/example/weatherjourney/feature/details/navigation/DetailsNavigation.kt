package com.example.weatherjourney.feature.details.navigation

import kotlinx.serialization.Serializable

@Serializable
data class DetailsRoute(
    val isCurrentLocation: Boolean = false,
    val locationId: Int? = null
) {
    init {
        require(if (isCurrentLocation) locationId == null else locationId != null) {
            "LocationId must be null if isCurrentLocation is false"
        }
    }
}
