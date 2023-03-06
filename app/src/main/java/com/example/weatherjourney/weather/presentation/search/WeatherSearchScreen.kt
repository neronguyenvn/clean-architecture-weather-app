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
import androidx.compose.runtime.getValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.weatherjourney.R
import com.example.weatherjourney.presentation.component.LoadingContent
import com.example.weatherjourney.util.ActionLabel
import com.example.weatherjourney.weather.domain.model.CityUiModel
import com.example.weatherjourney.weather.domain.model.SavedCity
import com.example.weatherjourney.weather.domain.model.SuggestionCity
import com.example.weatherjourney.weather.presentation.search.component.SavedCityItem
import com.example.weatherjourney.weather.presentation.search.component.SearchBar
import com.example.weatherjourney.weather.presentation.search.component.SuggestionCityItem

private enum class SearchScreenType {
    SavedInfoFeed,
    SuggestionFeed,
    NoResult
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun WeatherSearchScreen(
    snackbarHostState: SnackbarHostState,
    onBackClick: () -> Unit,
    onItemClick: (CityUiModel) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: WeatherSearchViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        modifier = modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            SearchBar(
                value = uiState.input,
                onBackClick = onBackClick,
                onValueChange = viewModel::onInputUpdate,
                onValueClear = { viewModel.onInputUpdate("") },
                modifier = Modifier.padding(top = 8.dp)
            )
        }
    ) { paddingValues ->
        WeatherSearchScreenContent(
            uiState = uiState,
            onRefresh = viewModel::onRefresh,
            onCityClick = onItemClick,
            onCityLongClick = viewModel::onSavedCityLongClick,
            modifier = Modifier.padding(paddingValues)
        )

        uiState.userMessage?.let { userMessage ->
            val snackbarText = userMessage.message.asString()
            val actionLabel = userMessage.actionLabel.label?.asString()
            val keyboardController = LocalSoftwareKeyboardController.current
            val haptic = LocalHapticFeedback.current

            LaunchedEffect(snackbarHostState, snackbarText, actionLabel) {
                keyboardController?.hide()

                if (userMessage.actionLabel == ActionLabel.DELETE) {
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                }

                val result = snackbarHostState.showSnackbar(
                    message = snackbarText,
                    actionLabel = actionLabel,
                    duration = SnackbarDuration.Short
                )

                if (result == SnackbarResult.ActionPerformed) {
                    viewModel.onDeleteLocation()
                }

                viewModel.snackbarMessageShown()
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
    val cities = when (uiState) {
        is WeatherSearchUiState.ShowSaveCities -> uiState.savedCities
        is WeatherSearchUiState.ShowSuggestionCities -> uiState.suggestionCities
    }

    when (getSearchScreenType(uiState)) {
        SearchScreenType.SavedInfoFeed -> {
            SavedCitiesContent(
                savedCities = cities as List<SavedCity>,
                isLoading = uiState.isLoading,
                onRefresh = onRefresh,
                onCityClick = onCityClick,
                onCityLongClick = onCityLongClick,
                modifier
            )
        }

        SearchScreenType.NoResult -> {
            Text(
                stringResource(R.string.no_result),
                style = MaterialTheme.typography.titleMedium,
                textAlign = TextAlign.Center,
                modifier = modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp)
            )
        }

        SearchScreenType.SuggestionFeed -> {
            LazyColumn(modifier.fillMaxWidth()) {
                items(cities as List<SuggestionCity>) { city ->
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
                    onCityLongClick = { onCityLongClick(city) }
                )
            }
        }
    }
}

private fun getSearchScreenType(uiState: WeatherSearchUiState): SearchScreenType =
    when (uiState) {
        is WeatherSearchUiState.ShowSaveCities -> SearchScreenType.SavedInfoFeed
        is WeatherSearchUiState.ShowSuggestionCities ->
            if (uiState.suggestionCities.isEmpty()) {
                SearchScreenType.NoResult
            } else {
                SearchScreenType.SuggestionFeed
            }
    }
