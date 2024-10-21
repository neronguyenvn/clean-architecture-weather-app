package com.example.weatherjourney.feature.search.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.example.weatherjourney.feature.search.SearchRoute
import kotlinx.serialization.Serializable

@Serializable
data object SearchRoute

fun NavController.navigateToSearch() {
    navigate(SearchRoute)
}

fun NavGraphBuilder.searchScreen(onBackClick: () -> Unit) {
    composable<SearchRoute> {
        SearchRoute(onBackClick = onBackClick)
    }
}

