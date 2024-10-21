package com.example.weatherjourney.app.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.example.weatherjourney.feature.details.navigation.detailsScreen
import com.example.weatherjourney.feature.details.navigation.navigateToLocationDetails
import com.example.weatherjourney.feature.home.navigation.HomeRoute
import com.example.weatherjourney.feature.home.navigation.homeScreen
import com.example.weatherjourney.feature.search.navigation.navigateToSearch
import com.example.weatherjourney.feature.search.navigation.searchScreen
import com.example.weatherjourney.feature.settings.navigation.navigateToSettings
import com.example.weatherjourney.feature.settings.navigation.settingsScreen

@Composable
fun WeatherNavGraph(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
) {
    NavHost(
        navController = navController,
        startDestination = HomeRoute,
        modifier = modifier,
    ) {
        homeScreen(
            onSearchClick = navController::navigateToSearch,
            onLocationClick = { navController.navigateToLocationDetails(it) }
        )
        detailsScreen(
            onBackClick = navController::popBackStack,
            onSettingsClick = navController::navigateToSettings
        )
        searchScreen(onBackClick = navController::popBackStack)
        settingsScreen(onBackClick = navController::popBackStack)
    }
}
