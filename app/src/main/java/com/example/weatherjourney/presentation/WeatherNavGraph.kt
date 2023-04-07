package com.example.weatherjourney.presentation

import android.app.Activity
import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.example.weatherjourney.features.recommendation.presentation.RecommendationScreen
import com.example.weatherjourney.features.weather.domain.model.Coordinate
import com.example.weatherjourney.features.weather.presentation.info.WeatherInfoScreen
import com.example.weatherjourney.features.weather.presentation.search.WeatherSearchScreen
import com.example.weatherjourney.features.weather.presentation.setting.WeatherSettingScreen
import com.example.weatherjourney.presentation.WeatherDestinationsArgs.CITY_ADDRESS_ARG
import com.example.weatherjourney.presentation.WeatherDestinationsArgs.COUNTRY_CODE_ARG
import com.example.weatherjourney.presentation.WeatherDestinationsArgs.LATITUDE_ARG
import com.example.weatherjourney.presentation.WeatherDestinationsArgs.LONGITUDE_ARG
import com.example.weatherjourney.presentation.WeatherDestinationsArgs.NAVIGATION_KEY_ARG
import com.example.weatherjourney.presentation.WeatherDestinationsArgs.TIMEZONE_ARG
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.composable
import com.google.accompanist.navigation.animation.rememberAnimatedNavController

private const val ANIMATION_DURATION = 400

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun WeatherNavGraph(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberAnimatedNavController(),
    startDestination: String = WeatherDestinations.INFO_ROUTE,
    navActions: WeatherNavigationActions = remember(navController) {
        WeatherNavigationActions(navController)
    }
) {
    val snackbarHostState = remember { SnackbarHostState() }

    AnimatedNavHost(
        navController = navController,
        startDestination = startDestination,
        enterTransition = {
            slideIntoContainer(
                AnimatedContentScope.SlideDirection.Left,
                animationSpec = tween(ANIMATION_DURATION)
            )
        },
        exitTransition = {
            slideOutOfContainer(
                AnimatedContentScope.SlideDirection.Left,
                animationSpec = tween(ANIMATION_DURATION)
            )
        },
        popEnterTransition = {
            slideIntoContainer(
                AnimatedContentScope.SlideDirection.Right,
                animationSpec = tween(ANIMATION_DURATION)
            )
        },
        popExitTransition = {
            slideOutOfContainer(
                AnimatedContentScope.SlideDirection.Right,
                animationSpec = tween(ANIMATION_DURATION)
            )
        },
        modifier = modifier
    ) {
        composable(
            WeatherDestinations.INFO_ROUTE,
            exitTransition = {
                when (targetState.destination.route) {
                    WeatherDestinations.SEARCH_ROUTE -> slideOutOfContainer(
                        AnimatedContentScope.SlideDirection.Right,
                        animationSpec = tween(ANIMATION_DURATION)
                    )

                    else -> null
                }
            },
            popEnterTransition = {
                when (initialState.destination.route) {
                    WeatherDestinations.SEARCH_ROUTE -> slideIntoContainer(
                        AnimatedContentScope.SlideDirection.Left,
                        animationSpec = tween(ANIMATION_DURATION)
                    )

                    else -> null
                }
            },
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
                    onSearchClick = {
                        snackbarHostState.currentSnackbarData?.dismiss()
                        navActions.navigateToSearch()
                    },
                    onSettingClick = {
                        snackbarHostState.currentSnackbarData?.dismiss()
                        navActions.navigateToSetting()
                    },
                    onNotificationClick = {
                        snackbarHostState.currentSnackbarData?.dismiss()
                        navActions.navigateToNotification()
                    },
                    onNavigationToInfoDone = { entry.arguments?.putInt(NAVIGATION_KEY_ARG, 0) }
                )
            }
        }
        composable(
            WeatherDestinations.SEARCH_ROUTE,
            enterTransition = {
                slideIntoContainer(
                    AnimatedContentScope.SlideDirection.Right,
                    animationSpec = tween(ANIMATION_DURATION)
                )
            },
            popExitTransition = {
                slideOutOfContainer(
                    AnimatedContentScope.SlideDirection.Left,
                    animationSpec = tween(ANIMATION_DURATION)
                )
            }
        ) {
            WeatherSearchScreen(
                snackbarHostState = snackbarHostState,
                onBackClick = {
                    snackbarHostState.currentSnackbarData?.dismiss()
                    navController.popBackStack()
                },
                onItemClick = {
                    snackbarHostState.currentSnackbarData?.dismiss()
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
            RecommendationScreen(
                snackbarHostState = snackbarHostState,
                onBackClick = {
                    snackbarHostState.currentSnackbarData?.dismiss()
                    navController.popBackStack()
                }
            )
        }
    }
}

// Keys for navigation
const val NAVIGATE_FROM_SEARCH = Activity.RESULT_FIRST_USER + 1
