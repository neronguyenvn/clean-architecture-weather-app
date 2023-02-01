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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material3.Icon
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.weatherjourney.R
import com.example.weatherjourney.presentation.theme.Black30
import com.example.weatherjourney.util.UiEvent
import com.example.weatherjourney.weather.domain.model.CityUiModel
import com.example.weatherjourney.weather.domain.model.SavedCity
import com.example.weatherjourney.weather.domain.model.SuggestionCity
import com.example.weatherjourney.weather.presentation.search.component.SearchBar

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

@Composable
fun SuggestionCityItem(
    city: SuggestionCity,
    modifier: Modifier = Modifier,
    onCityClick: (SuggestionCity) -> Unit
) {
    Column(
        modifier = modifier
            .clickable { onCityClick(city) }
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        Spacer(Modifier.height(12.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(city.countryFlag, style = MaterialTheme.typography.bodyMedium)
            Spacer(Modifier.width(8.dp))
            Text(city.location, style = MaterialTheme.typography.bodyMedium)
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

@Composable
fun SavedCityItem(
    city: SavedCity,
    modifier: Modifier = Modifier,
    onCityClick: (SavedCity) -> Unit
) {
    Row(
        modifier = modifier
            .clickable { onCityClick(city) }
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (city.isCurrentLocation) {
            Icon(Icons.Outlined.LocationOn, null)
            Spacer(Modifier.width(4.dp))
        }
        Text(
            text = stringResource(R.string.daily_weather, city.location, city.weather),
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.weight(10f)
        )
        Spacer(Modifier.weight(1f))
        Text(
            stringResource(R.string.temperature, city.temp),
            style = MaterialTheme.typography.bodyMedium
        )
        Spacer(Modifier.width(4.dp))
        AsyncImage(
            model = ImageRequest.Builder(context = LocalContext.current)
                .data(city.imageUrl)
                .crossfade(true)
                .build(),
            placeholder = painterResource(R.drawable.ic_loading),
            error = painterResource(R.drawable.ic_broken_image),
            contentDescription = null
        )
    }
}
