package com.example.weatherjourney.presentation

import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import com.example.weatherjourney.presentation.WeatherDestinationsArgs.CITY_ADDRESS_ARG
import com.example.weatherjourney.presentation.WeatherDestinationsArgs.LATITUDE_ARG
import com.example.weatherjourney.presentation.WeatherDestinationsArgs.LONGITUDE_ARG
import com.example.weatherjourney.presentation.WeatherDestinationsArgs.TIMEZONE_ARG
import com.example.weatherjourney.presentation.WeatherScreens.INFO_SCREEN
import com.example.weatherjourney.presentation.WeatherScreens.SEARCH_SCREEN
import com.example.weatherjourney.weather.domain.model.Coordinate

object WeatherScreens {
    const val INFO_SCREEN = "infoScreen"
    const val SEARCH_SCREEN = "searchScreen"
}

object WeatherDestinationsArgs {
    const val CITY_ADDRESS_ARG = "cityAddress"
    const val LATITUDE_ARG = "latitude"
    const val LONGITUDE_ARG = "longitude"
    const val TIMEZONE_ARG = "timeZone"
}

object WeatherDestinations {
    const val INFO_ROUTE =
        "$INFO_SCREEN?$CITY_ADDRESS_ARG={$CITY_ADDRESS_ARG}&$LATITUDE_ARG={$LATITUDE_ARG}&$LONGITUDE_ARG={$LONGITUDE_ARG}&$TIMEZONE_ARG={$TIMEZONE_ARG}"
    const val SEARCH_ROUTE = SEARCH_SCREEN
}

class WeatherNavigationActions(private val navController: NavController) {

    fun navigateToInfo(cityAddress: String, coordinate: Coordinate, timeZone: String) {
        navController.navigate(
            INFO_SCREEN.let {
                "$it?$CITY_ADDRESS_ARG=$cityAddress&$LATITUDE_ARG=${coordinate.lat}&$LONGITUDE_ARG=${coordinate.long}&$TIMEZONE_ARG=$timeZone"
            }
        ) {
            popUpTo(navController.graph.findStartDestination().id)
        }
    }

    fun navigateToSearch() {
        navController.navigate(WeatherDestinations.SEARCH_ROUTE)
    }
}
