package com.example.weatherjourney.weather.presentation.search

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.weatherjourney.presentation.theme.Black30
import com.example.weatherjourney.util.UiEvent
import com.example.weatherjourney.weather.domain.model.SuggestionCity
import com.example.weatherjourney.weather.presentation.search.component.SearchBar

@Composable
fun WeatherSearchScreen(
    snackbarHostState: SnackbarHostState,
    onBackClick: () -> Unit,
    onItemClick: (SuggestionCity) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: WeatherSearchViewModel = hiltViewModel()
) {
    val uiState = viewModel.uiState
    val context = LocalContext.current

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        snackbarHost = { SnackbarHost(snackbarHostState) },
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
            modifier = Modifier.padding(paddingValues)
        )

        LaunchedEffect(true) {
            viewModel.uiEvent.collect { event ->
                when (event) {
                    is UiEvent.ShowSnackbar -> snackbarHostState.showSnackbar(
                        event.message.asString(context)
                    )

                    else -> Unit
                }
            }
        }
    }
}

@Composable
fun WeatherSearchScreenContent(
    suggestionCities: List<SuggestionCity>,
    onCityClick: (SuggestionCity) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxWidth()
    ) {
        if (suggestionCities.isEmpty()) {
            item { }
        } else {
            items(suggestionCities) { city ->
                SuggestionCityItem(city = city) { selectedCity ->
                    onCityClick(selectedCity)
                }
            }
        }
    }
}

@Composable
fun SuggestionCityItem(city: SuggestionCity, onCityClick: (SuggestionCity) -> Unit) {
    Column(
        modifier = Modifier
            .clickable(onClick = { onCityClick(city) })
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        Spacer(Modifier.height(12.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(city.countryFlag, style = MaterialTheme.typography.bodyLarge)
            Spacer(Modifier.width(8.dp))
            Text(city.formattedLocationString, style = MaterialTheme.typography.bodyMedium)
        }
        Spacer(Modifier.height(12.dp))
        Spacer(
            Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(Black30)
        )
    }
}
