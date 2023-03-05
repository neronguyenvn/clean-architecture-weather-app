package com.example.weatherjourney.presentation

import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import com.example.weatherjourney.presentation.WeatherDestinationsArgs.CITY_ADDRESS_ARG
import com.example.weatherjourney.presentation.WeatherDestinationsArgs.LATITUDE_ARG
import com.example.weatherjourney.presentation.WeatherDestinationsArgs.LONGITUDE_ARG
import com.example.weatherjourney.presentation.WeatherDestinationsArgs.NAVIGATION_KEY_ARG
import com.example.weatherjourney.presentation.WeatherDestinationsArgs.TIMEZONE_ARG
import com.example.weatherjourney.presentation.WeatherScreens.INFO_SCREEN
import com.example.weatherjourney.presentation.WeatherScreens.NOTIFICATION_SCREEN
import com.example.weatherjourney.presentation.WeatherScreens.SEARCH_SCREEN
import com.example.weatherjourney.presentation.WeatherScreens.SETTING_SCREEN
import com.example.weatherjourney.weather.domain.model.Coordinate

object WeatherScreens {
    const val INFO_SCREEN = "infoScreen"
    const val SEARCH_SCREEN = "searchScreen"
    const val SETTING_SCREEN = "settingScreen"
    const val NOTIFICATION_SCREEN = "notificationScreen"
}

object WeatherDestinationsArgs {
    const val CITY_ADDRESS_ARG = "cityAddress"
    const val LATITUDE_ARG = "latitude"
    const val LONGITUDE_ARG = "longitude"
    const val TIMEZONE_ARG = "timeZone"
    const val NAVIGATION_KEY_ARG = "navigationKey"
}

object WeatherDestinations {
    const val INFO_ROUTE =
        "$INFO_SCREEN?$CITY_ADDRESS_ARG={$CITY_ADDRESS_ARG}" +
            "&$LATITUDE_ARG={$LATITUDE_ARG}" +
            "&$LONGITUDE_ARG={$LONGITUDE_ARG}" +
            "&$TIMEZONE_ARG={$TIMEZONE_ARG}" +
            "$NAVIGATION_KEY_ARG={$NAVIGATION_KEY_ARG}"
    const val SEARCH_ROUTE = SEARCH_SCREEN
    const val SETTING_ROUTE = SETTING_SCREEN
    const val NOTIFICATION_ROUTE = NOTIFICATION_SCREEN
}

class WeatherNavigationActions(private val navController: NavController) {

    fun navigateToInfo(
        cityAddress: String,
        coordinate: Coordinate,
        timeZone: String,
        navigationKey: Int = 0
    ) {
        navController.navigate(
            INFO_SCREEN.let {
                "$it?$CITY_ADDRESS_ARG=$cityAddress" +
                    "&$LATITUDE_ARG=${coordinate.latitude}" +
                    "&$LONGITUDE_ARG=${coordinate.longitude}" +
                    "&$TIMEZONE_ARG=$timeZone" +
                    "&$NAVIGATION_KEY_ARG=$navigationKey"
            }
        ) {
            popUpTo(navController.graph.findStartDestination().id)
        }
    }

    fun navigateToSearch() {
        navController.navigate(WeatherDestinations.SEARCH_ROUTE)
    }

    fun navigateToSetting() {
        navController.navigate(WeatherDestinations.SETTING_ROUTE)
    }

    fun navigateToNotification() {
        navController.navigate(WeatherDestinations.NOTIFICATION_ROUTE)
    }
}
