package com.example.weatherjourney.app.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import com.example.weatherjourney.app.navigation.WtnDestinationsArgs.COUNTRY_CODE_ARG
import com.example.weatherjourney.app.navigation.WtnDestinationsArgs.LATITUDE_ARG
import com.example.weatherjourney.app.navigation.WtnDestinationsArgs.LONGITUDE_ARG
import com.example.weatherjourney.app.navigation.WtnDestinationsArgs.NAVIGATION_KEY_ARG
import com.example.weatherjourney.app.navigation.WtnScreens.INFO_SCREEN
import com.example.weatherjourney.app.navigation.WtnScreens.SEARCH_SCREEN
import com.example.weatherjourney.app.navigation.WtnScreens.SETTING_SCREEN
import com.example.weatherjourney.core.model.location.Coordinate

object WtnScreens {
    const val INFO_SCREEN = "infoScreen"
    const val SEARCH_SCREEN = "searchScreen"
    const val SETTING_SCREEN = "settingScreen"
}

object WtnDestinationsArgs {
    const val LATITUDE_ARG = "latitude"
    const val LONGITUDE_ARG = "longitude"
    const val NAVIGATION_KEY_ARG = "navigationKey"
    const val COUNTRY_CODE_ARG = "countryCode"
}

object WtnDestinations {
    const val INFO_ROUTE =
        "$INFO_SCREEN?$COUNTRY_CODE_ARG={$COUNTRY_CODE_ARG}" +
            "&$LATITUDE_ARG={$LATITUDE_ARG}" +
            "&$LONGITUDE_ARG={$LONGITUDE_ARG}" +
            "&$NAVIGATION_KEY_ARG={$NAVIGATION_KEY_ARG}"

    const val SEARCH_ROUTE = SEARCH_SCREEN
    const val SETTING_ROUTE = SETTING_SCREEN
}

class WtnNavigationActions(private val navController: NavController) {

    fun navigateToInfo(
        coordinate: Coordinate,
        countryCode: String,
        navigationKey: Int = 0,
    ) {
        navController.navigate(
            INFO_SCREEN.let {
                "$it?$COUNTRY_CODE_ARG=$countryCode" +
                    "&$LATITUDE_ARG=${coordinate.latitude}" +
                    "&$LONGITUDE_ARG=${coordinate.longitude}" +
                    "&$NAVIGATION_KEY_ARG=$navigationKey"
            },
        ) {
            popUpTo(navController.graph.findStartDestination().id)
        }
    }

    fun navigateToSearch() {
        navController.navigate(WtnDestinations.SEARCH_ROUTE)
    }

    fun navigateToSetting() {
        navController.navigate(WtnDestinations.SETTING_ROUTE)
    }
}
