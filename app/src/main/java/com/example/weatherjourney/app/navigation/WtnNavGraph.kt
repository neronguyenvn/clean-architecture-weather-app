package com.example.weatherjourney.app.navigation

import android.app.Activity
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.weatherjourney.app.navigation.WtnDestinationsArgs.COUNTRY_CODE_ARG
import com.example.weatherjourney.app.navigation.WtnDestinationsArgs.LATITUDE_ARG
import com.example.weatherjourney.app.navigation.WtnDestinationsArgs.LONGITUDE_ARG
import com.example.weatherjourney.app.navigation.WtnDestinationsArgs.NAVIGATION_KEY_ARG
import com.example.weatherjourney.core.model.location.Coordinate
import com.example.weatherjourney.features.weather.info.WeatherInfoScreen
import com.example.weatherjourney.features.weather.search.WeatherSearchScreen
import com.example.weatherjourney.features.weather.setting.WeatherSettingScreen

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
        composable(
            WtnDestinations.INFO_ROUTE,
            arguments = listOf(
                navArgument(LATITUDE_ARG) { type = NavType.FloatType },
                navArgument(LONGITUDE_ARG) { type = NavType.FloatType },
                navArgument(COUNTRY_CODE_ARG) { type = NavType.StringType },
                navArgument(NAVIGATION_KEY_ARG) {
                    type = NavType.IntType; defaultValue = 0
                },
            ),
        ) { entry ->
            entry.arguments?.let {
                val latitude = it.getFloat(LATITUDE_ARG)
                val longitude = it.getFloat(LONGITUDE_ARG)
                val navigationKey = it.getInt(NAVIGATION_KEY_ARG)
                val countryCode = it.getString(COUNTRY_CODE_ARG) ?: ""

                WeatherInfoScreen(
                    coordinate = Coordinate(latitude.toDouble(), longitude.toDouble()),
                    snackbarHostState = snackbarHostState,
                    navigationKey = navigationKey,
                    countryCode = countryCode,
                    onSearchClick = {
                        snackbarHostState.currentSnackbarData?.dismiss()
                        navActions.navigateToSearch()
                    },
                    onSettingClick = {
                        snackbarHostState.currentSnackbarData?.dismiss()
                        navActions.navigateToSetting()
                    },
                    onNavigationToInfoDone = { entry.arguments?.putInt(NAVIGATION_KEY_ARG, 0) },
                )
            }
        }
        composable(WtnDestinations.SEARCH_ROUTE) {
            WeatherSearchScreen(
                snackbarHostState = snackbarHostState,
                onBackClick = {
                    navController.popBackStack()
                },
                onItemClick = {
                    navActions.navigateToInfo(
                        countryCode = it.countryCode,
                        coordinate = it.coordinate,
                        navigationKey = NAVIGATE_FROM_SEARCH,
                    )
                },
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

// Keys for navigation
const val NAVIGATE_FROM_SEARCH = Activity.RESULT_FIRST_USER + 1
