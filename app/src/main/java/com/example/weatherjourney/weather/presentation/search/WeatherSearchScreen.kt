package com.example.weatherjourney.weather.presentation.search

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.weatherjourney.R
import com.example.weatherjourney.util.UiEvent
import com.example.weatherjourney.weather.domain.model.CityUiModel
import com.example.weatherjourney.weather.domain.model.SavedCity
import com.example.weatherjourney.weather.domain.model.SuggestionCity
import com.example.weatherjourney.weather.presentation.search.component.SavedCityItem
import com.example.weatherjourney.weather.presentation.search.component.SearchBar
import com.example.weatherjourney.weather.presentation.search.component.SuggestionCityItem

@Composable
fun WeatherSearchScreen(
    snackbarHostState: SnackbarHostState,
    onBackClick: () -> Unit,
    onItemClick: (CityUiModel) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: WeatherSearchViewModel = hiltViewModel()
) {
    val uiState = viewModel.uiState
    val context = LocalContext.current

    Scaffold(
        modifier = modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            SearchBar(
                value = uiState.city,
                onBackClick = onBackClick,
                onValueChange = { viewModel.onEvent(WeatherSearchEvent.OnCityUpdate(cityAddress = it)) },
                onValueClear = { viewModel.onEvent(WeatherSearchEvent.OnCityUpdate(cityAddress = "")) },
                modifier = Modifier.padding(top = 8.dp)
            )
        }
    ) { paddingValues ->
        WeatherSearchScreenContent(
            city = uiState.city,
            savedCities = uiState.savedCities,
            suggestionCities = uiState.suggestionCities,
            onCityClick = onItemClick,
            modifier = Modifier.padding(paddingValues)
        )

        LaunchedEffect(true) {
            viewModel.onEvent(WeatherSearchEvent.OnFetchWeatherOfSavedLocations)

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
    city: String,
    savedCities: List<SavedCity>,
    suggestionCities: List<SuggestionCity>,
    onCityClick: (CityUiModel) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxWidth()
    ) {
        if (city.isBlank()) {
            items(savedCities) { city ->
                SavedCityItem(city = city) { selectedCity ->
                    onCityClick(selectedCity)
                }
            }
        } else {
            if (suggestionCities.isEmpty()) {
                item {
                    Text(
                        stringResource(R.string.no_result),
                        style = MaterialTheme.typography.titleMedium,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp)
                    )
                }
            } else {
                items(suggestionCities) { city ->
                    SuggestionCityItem(city = city) { selectedCity ->
                        onCityClick(selectedCity)
                    }
                }
            }
        }
    }
}
