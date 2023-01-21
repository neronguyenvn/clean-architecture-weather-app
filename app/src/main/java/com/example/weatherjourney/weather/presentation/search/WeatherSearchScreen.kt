package com.example.weatherjourney.weather.presentation.search

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun WeatherSearchScreen(
    onItemClick: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: WeatherSearchViewModel = viewModel()
) {
    val uiState = viewModel.uiState

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        WeatherSearchScreenContent(
            modifier = Modifier.padding(paddingValues),
            isSearching = uiState.isSearching
        )
    }
}

@Composable
fun WeatherSearchScreenContent(modifier: Modifier, isSearching: Boolean) {
}
