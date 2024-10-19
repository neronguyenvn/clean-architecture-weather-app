package com.example.weatherjourney.feature.home.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.example.weatherjourney.feature.home.HomeScreen
import kotlinx.serialization.Serializable

@Serializable
data object HomeRoute

fun NavGraphBuilder.homeScreen(
    onLocationClick: (Int) -> Unit
) {
    composable<HomeRoute> {
        HomeScreen(
            onLocationClick = onLocationClick
        )
    }
}