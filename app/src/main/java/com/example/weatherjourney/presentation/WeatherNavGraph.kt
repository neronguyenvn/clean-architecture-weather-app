package com.example.weatherjourney.presentation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.weatherjourney.presentation.WeatherDestinationsArgs.CITY_ARG
import com.example.weatherjourney.presentation.WeatherDestinationsArgs.LATITUDE_ARG
import com.example.weatherjourney.presentation.WeatherDestinationsArgs.LONGITUDE_ARG
import com.example.weatherjourney.weather.domain.model.Coordinate
import com.example.weatherjourney.weather.presentation.info.WeatherInfoScreen
import com.example.weatherjourney.weather.presentation.search.WeatherSearchScreen

@Composable
fun WeatherNavGraph(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    startDestination: String = WeatherDestinations.INFO_ROUTE,
    navActions: WeatherNavigationActions = remember(navController) {
        WeatherNavigationActions(navController)
    }
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        composable(
            WeatherDestinations.INFO_ROUTE,
            arguments = listOf(
                navArgument(CITY_ARG) { type = NavType.StringType },
                navArgument(LATITUDE_ARG) { type = NavType.FloatType },
                navArgument(LONGITUDE_ARG) { type = NavType.FloatType }
            )
        ) { entry ->
            val city = entry.arguments?.getString(CITY_ARG) ?: ""
            val latitude = entry.arguments?.getFloat(LATITUDE_ARG) ?: 0.0
            val longitude = entry.arguments?.getFloat(LONGITUDE_ARG) ?: 0.0

            WeatherInfoScreen(
                city = city,
                coordinate = Coordinate(latitude.toDouble(), longitude.toDouble()),
                onSearchClick = { navActions.navigateToSearch() },
                onSettingClick = {}
            )
        }
        composable(WeatherDestinations.SEARCH_ROUTE) {
            WeatherSearchScreen(
                onBackClick = { navController.popBackStack() },
                onItemClick = {
                    navActions.navigateToInfo(
                        it.formattedLocationString,
                        it.latitude,
                        it.longitude
                    )
                }
            )
        }
    }
}
