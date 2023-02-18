package com.example.weatherjourney.weather.presentation.search

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.weatherjourney.R
import com.example.weatherjourney.presentation.component.LoadingContent
import com.example.weatherjourney.util.UiEvent
import com.example.weatherjourney.weather.domain.model.CityUiModel
import com.example.weatherjourney.weather.domain.model.SavedCity
import com.example.weatherjourney.weather.presentation.search.component.SavedCityItem
import com.example.weatherjourney.weather.presentation.search.component.SearchBar
import com.example.weatherjourney.weather.presentation.search.component.SuggestionCityItem

@OptIn(ExperimentalComposeUiApi::class)
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
                value = uiState.cityAddress,
                onBackClick = onBackClick,
                onValueChange = { viewModel.onEvent(WeatherSearchEvent.OnCityUpdate(cityAddress = it)) },
                onValueClear = { viewModel.onEvent(WeatherSearchEvent.OnCityUpdate(cityAddress = "")) },
                modifier = Modifier.padding(top = 8.dp)
            )
        }
    ) { paddingValues ->
        WeatherSearchScreenContent(
            uiState = uiState,
            onRefresh = { viewModel.onEvent(WeatherSearchEvent.OnRefresh) },
            onCityClick = onItemClick,
            onCityLongClick = { viewModel.onEvent(WeatherSearchEvent.OnCityLongClick(it)) },
            modifier = Modifier.padding(paddingValues)
        )

        val keyboardController = LocalSoftwareKeyboardController.current
        val haptic = LocalHapticFeedback.current

        LaunchedEffect(true) {
            viewModel.uiEvent.collect { event ->
                when (event) {
                    is UiEvent.ShowSnackbar -> {
                        when (event.actionLabel) {
                            R.string.delete -> {
                                keyboardController?.hide()
                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            }
                        }

                        val result = snackbarHostState.showSnackbar(
                            message = event.message.asString(context),
                            actionLabel = if (event.actionLabel == 0) {
                                null
                            } else {
                                context.getString(event.actionLabel)
                            },
                            duration = SnackbarDuration.Short
                        )

                        if (result == SnackbarResult.ActionPerformed) {
                            when (event.actionLabel) {
                                R.string.delete -> viewModel.onEvent(WeatherSearchEvent.OnCityDelete)
                            }
                        }
                    }

                    else -> Unit
                }
            }
        }
    }
}

@Composable
fun WeatherSearchScreenContent(
    uiState: WeatherSearchUiState,
    onRefresh: () -> Unit,
    onCityClick: (CityUiModel) -> Unit,
    onCityLongClick: (CityUiModel) -> Unit,
    modifier: Modifier = Modifier
) {
    if (uiState.cityAddress.isBlank()) {
        SavedCitiesContent(
            savedCities = uiState.savedCities,
            isLoading = uiState.isLoading,
            onRefresh = onRefresh,
            onCityClick = onCityClick,
            onCityLongClick = onCityLongClick,
            modifier
        )
    } else {
        LazyColumn(modifier.fillMaxWidth()) {
            if (uiState.suggestionCities.isEmpty()) {
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
                items(uiState.suggestionCities) { city ->
                    SuggestionCityItem(city = city) { selectedCity ->
                        onCityClick(selectedCity)
                    }
                }
            }
        }
    }
}

@Composable
fun SavedCitiesContent(
    savedCities: List<SavedCity>,
    isLoading: Boolean,
    onRefresh: () -> Unit,
    onCityClick: (CityUiModel) -> Unit,
    onCityLongClick: (CityUiModel) -> Unit,
    modifier: Modifier = Modifier
) {
    LoadingContent(
        isLoading = isLoading,
        onRefresh = onRefresh,
        modifier = modifier
    ) {
        LazyColumn(Modifier.fillMaxWidth()) {
            items(savedCities) { city ->
                SavedCityItem(
                    city = city,
                    onCityClick = { onCityClick(city) },
                    onCityLongClick = {
                        onCityLongClick(city)
                    }
                )
            }
        }
    }
}
