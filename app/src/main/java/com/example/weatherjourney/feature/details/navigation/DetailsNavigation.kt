package com.example.weatherjourney.feature.details.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.example.weatherjourney.feature.details.DetailsScreen
import kotlinx.serialization.Serializable

@Serializable
sealed interface DetailsRoute {

    data class LocationDetails(val locationId: Int) : DetailsRoute

    data object CurrentLocationDetails : DetailsRoute
}

fun NavController.navigateToCurrentLocationDetails() {
    navigate(route = DetailsRoute.CurrentLocationDetails)
}

fun NavController.navigateToLocationDetails(locationId: Int) {
    navigate(route = DetailsRoute.LocationDetails(locationId))
}

fun NavGraphBuilder.detailsScreen(route: DetailsRoute) {
    composable<DetailsRoute> {
        DetailsScreen()
    }
}
