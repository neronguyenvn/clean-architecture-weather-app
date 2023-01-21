package com.example.weatherjourney.presentation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.weatherjourney.weather.presentation.info.WeatherInfoScreen
import com.example.weatherjourney.weather.presentation.search.WeatherSearchScreen

@Composable
fun WeatherNavGraph(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    startDestination: String = WeatherScreens.INFO_SCREEN,
    navActions: WeatherNavigationActions = remember(navController) {
        WeatherNavigationActions(navController)
    }
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        composable(WeatherScreens.INFO_SCREEN) {
            WeatherInfoScreen(
                onSearchClick = { navActions.navigateToSearch() },
                onSettingClick = {}
            )
        }
        composable(WeatherScreens.SEARCH_SCREEN) {
            WeatherSearchScreen(onItemClick = {})
        }
    }
}
