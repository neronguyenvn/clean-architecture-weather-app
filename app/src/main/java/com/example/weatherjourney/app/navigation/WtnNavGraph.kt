package com.example.weatherjourney.app.navigation

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.weatherjourney.feature.details.WeatherInfoScreen
import com.example.weatherjourney.feature.search.WeatherSearchScreen
import com.example.weatherjourney.feature.setting.WeatherSettingScreen

@Composable
fun WtnNavGraph(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    startDestination: String = WtnDestinations.INFO_ROUTE,
    navActions: WtnNavigationActions = remember(navController) {
        WtnNavigationActions(navController)
    },
) {
    val snackbarHostState = remember { SnackbarHostState() }

    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier,
    ) {
        composable(WtnDestinations.INFO_ROUTE) {
            WeatherInfoScreen(
                snackbarHostState = snackbarHostState,
                onSearchClick = {
                    snackbarHostState.currentSnackbarData?.dismiss()
                    navActions.navigateToSearch()
                },
                onSettingClick = {
                    snackbarHostState.currentSnackbarData?.dismiss()
                    navActions.navigateToSetting()
                },
            )
        }
        composable(WtnDestinations.SEARCH_ROUTE) {
            WeatherSearchScreen(
                snackbarHostState = snackbarHostState,
                onBackClick = { navController.popBackStack() },
                navigateToInfo = { navActions.navigateToInfo() }
            )
        }
        composable(WtnDestinations.SETTING_ROUTE) {
            WeatherSettingScreen(
                snackbarHostState = snackbarHostState,
                onBackClick = { navController.popBackStack() },
            )
        }
    }
}
