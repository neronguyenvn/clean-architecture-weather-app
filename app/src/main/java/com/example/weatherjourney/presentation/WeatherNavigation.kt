package com.example.weatherjourney.presentation

import androidx.navigation.NavController

object WeatherScreens {
    const val INFO_SCREEN = "infoScreen"
    const val SEARCH_SCREEN = "searchScreen"
}

class WeatherNavigationActions(private val navController: NavController) {

    fun navigateToInfo() {
        navController.navigate(WeatherScreens.INFO_SCREEN)
    }

    fun navigateToSearch() {
        navController.navigate(WeatherScreens.SEARCH_SCREEN)
    }
}
