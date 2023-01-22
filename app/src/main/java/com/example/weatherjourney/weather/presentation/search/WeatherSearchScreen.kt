package com.example.weatherjourney.weather.presentation.search

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.weatherjourney.presentation.theme.Black30
import com.example.weatherjourney.weather.presentation.search.component.SearchBar

@Composable
fun WeatherSearchScreen(
    onBackClick: () -> Unit,
    onItemClick: (String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: WeatherSearchViewModel = viewModel()
) {
    val uiState = viewModel.uiState

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            SearchBar(
                value = uiState.city,
                onBackClick = onBackClick,
                onValueChange = { viewModel.onEvent(WeatherSearchEvent.OnCityUpdate(city = it)) },
                onValueClear = { viewModel.onEvent(WeatherSearchEvent.OnCityUpdate(city = "")) },
                modifier = Modifier.padding(top = 8.dp)
            )
        }
    ) { paddingValues ->
        WeatherSearchScreenContent(
            suggestionCities = uiState.suggestionCities,
            onCityClick = onItemClick,
            modifier = Modifier.padding(paddingValues),
            isSearching = uiState.isSearching
        )
    }
}

@Composable
fun WeatherSearchScreenContent(
    isSearching: Boolean,
    suggestionCities: List<String>,
    onCityClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxWidth()
    ) {
        if (suggestionCities.isEmpty()) {
            // TODO:  Show saved location
        } else {
            items(suggestionCities) { city ->
                SuggestionCityItem(
                    city = city,
                    onCityClick = { selectedCountry ->
                        onCityClick(selectedCountry)
                    }
                )
            }
        }
    }
}

@Composable
fun SuggestionCityItem(city: String, onCityClick: (String) -> Unit) {
    Column(
        modifier = Modifier
            .clickable(onClick = { onCityClick(city) })
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        Spacer(Modifier.height(12.dp))
        Text(city, style = MaterialTheme.typography.bodyLarge)
        Spacer(Modifier.height(12.dp))
        Spacer(
            Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(Black30)
        )
    }
}
