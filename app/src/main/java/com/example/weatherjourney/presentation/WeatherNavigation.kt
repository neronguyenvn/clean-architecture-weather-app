package com.example.weatherjourney.presentation

import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import com.example.weatherjourney.presentation.WeatherDestinationsArgs.CITY_ARG
import com.example.weatherjourney.presentation.WeatherDestinationsArgs.LATITUDE_ARG
import com.example.weatherjourney.presentation.WeatherDestinationsArgs.LONGITUDE_ARG
import com.example.weatherjourney.presentation.WeatherScreens.INFO_SCREEN
import com.example.weatherjourney.presentation.WeatherScreens.SEARCH_SCREEN

object WeatherScreens {
    const val INFO_SCREEN = "infoScreen"
    const val SEARCH_SCREEN = "searchScreen"
}

object WeatherDestinationsArgs {
    const val CITY_ARG = "city"
    const val LATITUDE_ARG = "latitude"
    const val LONGITUDE_ARG = "longitude"
}

object WeatherDestinations {
    const val INFO_ROUTE =
        "$INFO_SCREEN?$CITY_ARG={$CITY_ARG}&$LATITUDE_ARG={$LATITUDE_ARG}&$LONGITUDE_ARG={$LONGITUDE_ARG}"
    const val SEARCH_ROUTE = SEARCH_SCREEN
}

class WeatherNavigationActions(private val navController: NavController) {

    fun navigateToInfo(city: String, latitude: Double, longitude: Double) {
        navController.navigate(
            INFO_SCREEN.let {
                "$it?$CITY_ARG=$city&$LATITUDE_ARG=$latitude&$LONGITUDE_ARG=$longitude"
            }
        ) {
            popUpTo(navController.graph.findStartDestination().id)
        }
    }

    fun navigateToSearch() {
        navController.navigate(WeatherDestinations.SEARCH_ROUTE)
    }
}
