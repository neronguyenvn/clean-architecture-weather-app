package com.example.weatherjourney.presentation

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
import com.example.weatherjourney.presentation.WeatherDestinationsArgs.CITY_ADDRESS_ARG
import com.example.weatherjourney.presentation.WeatherDestinationsArgs.COUNTRY_CODE_ARG
import com.example.weatherjourney.presentation.WeatherDestinationsArgs.LATITUDE_ARG
import com.example.weatherjourney.presentation.WeatherDestinationsArgs.LONGITUDE_ARG
import com.example.weatherjourney.presentation.WeatherDestinationsArgs.NAVIGATION_KEY_ARG
import com.example.weatherjourney.presentation.WeatherDestinationsArgs.TIMEZONE_ARG
import com.example.weatherjourney.weather.domain.model.Coordinate
import com.example.weatherjourney.weather.presentation.info.WeatherInfoScreen
import com.example.weatherjourney.weather.presentation.notification.WeatherNotificationScreen
import com.example.weatherjourney.weather.presentation.search.WeatherSearchScreen
import com.example.weatherjourney.weather.presentation.setting.WeatherSettingScreen

@Composable
fun WeatherNavGraph(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    startDestination: String = WeatherDestinations.INFO_ROUTE,
    navActions: WeatherNavigationActions = remember(navController) {
        WeatherNavigationActions(navController)
    }
) {
    val snackbarHostState = remember { SnackbarHostState() }

    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        composable(
            WeatherDestinations.INFO_ROUTE,
            arguments = listOf(
                navArgument(CITY_ADDRESS_ARG) { type = NavType.StringType },
                navArgument(LATITUDE_ARG) { type = NavType.FloatType },
                navArgument(LONGITUDE_ARG) { type = NavType.FloatType },
                navArgument(TIMEZONE_ARG) { type = NavType.StringType },
                navArgument(NAVIGATION_KEY_ARG) {
                    type = NavType.IntType; defaultValue = 0
                }
            )
        ) { entry ->
            entry.arguments?.let {
                val city = it.getString(CITY_ADDRESS_ARG) ?: ""
                val latitude = it.getFloat(LATITUDE_ARG)
                val longitude = it.getFloat(LONGITUDE_ARG)
                val timeZone = it.getString(TIMEZONE_ARG) ?: ""
                val navigationKey = it.getInt(NAVIGATION_KEY_ARG)
                val countryCode = it.getString(COUNTRY_CODE_ARG) ?: ""

                WeatherInfoScreen(
                    city = city,
                    coordinate = Coordinate(latitude.toDouble(), longitude.toDouble()),
                    timeZone = timeZone,
                    snackbarHostState = snackbarHostState,
                    navigationKey = navigationKey,
                    countryCode = countryCode,
                    onSearchClick = { navActions.navigateToSearch() },
                    onSettingClick = { navActions.navigateToSetting() },
                    onNotificationClick = { navActions.navigateToNotification() }
                )
            }
        }
        composable(WeatherDestinations.SEARCH_ROUTE) {
            WeatherSearchScreen(
                snackbarHostState = snackbarHostState,
                onBackClick = { navController.popBackStack() },
                onItemClick = {
                    navActions.navigateToInfo(
                        it.cityAddress,
                        it.coordinate,
                        it.timeZone,
                        it.countryCode,
                        NAVIGATE_FROM_SEARCH
                    )
                }
            )
        }
        composable(WeatherDestinations.SETTING_ROUTE) {
            WeatherSettingScreen(
                snackbarHostState = snackbarHostState,
                onBackClick = { navController.popBackStack() }
            )
        }
        composable(WeatherDestinations.NOTIFICATION_ROUTE) {
            WeatherNotificationScreen(
                snackbarHostState = snackbarHostState,
                onBackClick = { navController.popBackStack() }
            )
        }
    }
}

// Keys for navigation
const val NAVIGATE_FROM_SEARCH = Activity.RESULT_FIRST_USER + 1
