package com.example.weatherjourney.feature.settings.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.example.weatherjourney.feature.settings.SettingsRoute
import kotlinx.serialization.Serializable

@Serializable
data object SettingsRoute

fun NavController.navigateToSettings() {
    navigate(SettingsRoute)
}

fun NavGraphBuilder.settingsScreen(onBackClick: () -> Unit) {
    composable<SettingsRoute> {
        SettingsRoute(onBackClick = onBackClick)
    }
}
