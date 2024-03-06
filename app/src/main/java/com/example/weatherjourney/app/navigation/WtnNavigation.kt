package com.example.weatherjourney.app.navigation

import androidx.navigation.NavController
import com.example.weatherjourney.app.navigation.WtnScreens.INFO_SCREEN
import com.example.weatherjourney.app.navigation.WtnScreens.SEARCH_SCREEN
import com.example.weatherjourney.app.navigation.WtnScreens.SETTING_SCREEN

object WtnScreens {
    const val INFO_SCREEN = "infoScreen"
    const val SEARCH_SCREEN = "searchScreen"
    const val SETTING_SCREEN = "settingScreen"
}

object WtnDestinations {
    const val INFO_ROUTE = INFO_SCREEN
    const val SEARCH_ROUTE = SEARCH_SCREEN
    const val SETTING_ROUTE = SETTING_SCREEN
}

class WtnNavigationActions(private val navController: NavController) {

    fun navigateToInfo() {
        navController.navigate(WtnDestinations.INFO_ROUTE)
    }

    fun navigateToSearch() {
        navController.navigate(WtnDestinations.SEARCH_ROUTE)
    }

    fun navigateToSetting() {
        navController.navigate(WtnDestinations.SETTING_ROUTE)
    }
}
