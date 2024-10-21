package com.example.weatherjourney.feature.details.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.example.weatherjourney.feature.details.DetailsScreen
import kotlinx.serialization.Serializable

sealed interface DetailsRoute {

    @Serializable
    data class LocationDetails(val locationId: Int) : DetailsRoute

    @Serializable
    data object CurrentLocationDetails : DetailsRoute

    @Serializable
    data object Placeholder : DetailsRoute
}

fun NavController.navigateToCurrentLocationDetails() {
    navigate(route = DetailsRoute.CurrentLocationDetails)
}

fun NavController.navigateToLocationDetails(locationId: Int) {
    navigate(route = DetailsRoute.LocationDetails(locationId))
}

fun NavGraphBuilder.detailsScreen(
    onBackClick: () -> Unit,
    onSettingsClick: () -> Unit
) {
    composable<DetailsRoute.Placeholder> {
        DetailsScreen(
            onBackClick = onBackClick,
            onSettingsClick = onSettingsClick
        )
    }
}
