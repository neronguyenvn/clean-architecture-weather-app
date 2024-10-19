package com.example.weatherjourney.feature.search.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.example.weatherjourney.feature.search.SearchRoute
import kotlinx.serialization.Serializable

@Serializable
data object SearchRoute

fun NavGraphBuilder.searchScreen() {
    composable<SearchRoute> {
        SearchRoute()
    }
}
