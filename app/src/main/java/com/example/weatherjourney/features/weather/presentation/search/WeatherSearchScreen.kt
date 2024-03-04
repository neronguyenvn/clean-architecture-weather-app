package com.example.weatherjourney.features.weather.presentation.search

import android.Manifest
import android.app.Activity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.weatherjourney.R
import com.example.weatherjourney.features.weather.domain.model.CityUiModel
import com.example.weatherjourney.features.weather.domain.model.SavedCity
import com.example.weatherjourney.features.weather.presentation.search.component.CurrentLocationField
import com.example.weatherjourney.features.weather.presentation.search.component.SavedCityItem
import com.example.weatherjourney.features.weather.presentation.search.component.SearchBar
import com.example.weatherjourney.features.weather.presentation.search.component.SuggestionCityItem
import com.example.weatherjourney.presentation.component.LoadingContent
import com.example.weatherjourney.util.UserMessage
import com.example.weatherjourney.util.UserMessage.RequestingTurnOnLocationService
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.Priority

private enum class SearchScreenType {
    SavedInfoFeed,
    SavedInfoFeedWithYourLocationButton,
    SuggestionFeed,
    NoResult,
}

@OptIn(ExperimentalPermissionsApi::class)
@Suppress("LongMethod", "MagicNumber")
@Composable
fun WeatherSearchScreen(
    snackbarHostState: SnackbarHostState,
    onBackClick: () -> Unit,
    onItemClick: (CityUiModel) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: WeatherSearchViewModel = hiltViewModel(),
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
                modifier = Modifier.padding(top = 8.dp),
            )
        },
    ) { paddingValues ->

        val keyboardController = LocalSoftwareKeyboardController.current
        val haptic = LocalHapticFeedback.current
        val context = LocalContext.current

        val activityResultLauncher = rememberLauncherForActivityResult(
            ActivityResultContracts.StartIntentSenderForResult(),
        ) { result ->
            viewModel.onPermissionActionResult(result.resultCode == Activity.RESULT_OK, true)
        }

        val locationPermissionState = rememberMultiplePermissionsState(
            listOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
            ),
        ) { viewModel.onPermissionActionResult(it.any { permission -> permission.value }) }

        WeatherSearchScreenContent(
            uiState = uiState,
            onRefresh = viewModel::onRefresh,
            onCityClick = { viewModel.onItemClick(it, onItemClick) },
            onCityLongClick = viewModel::onSavedCityLongClick,
            onCurrentLocationFieldClick = viewModel::onLocationFieldClick,
            modifier = Modifier.padding(paddingValues),
        )

        uiState.userMessage?.let { userMessage ->
            if (userMessage is RequestingTurnOnLocationService) {
                val client = LocationServices.getSettingsClient(context)
                val task = client.checkLocationSettings(
                    LocationSettingsRequest.Builder()
                        .addLocationRequest(
                            LocationRequest.Builder(
                                Priority.PRIORITY_HIGH_ACCURACY,
                                5000,
                            ).build(),
                        )
                        .build(),
                )
                task.addOnFailureListener { exception ->
                    if (exception is ResolvableApiException) {
                        // Location is not enabled, show the dialog to turn it on
                        val intentSender = exception.resolution.intentSender
                        val intent = IntentSenderRequest.Builder(intentSender).build()
                        activityResultLauncher.launch(intent)
                    }
                }
            }

            if (userMessage is UserMessage.RequestingLocationPermission) {
                if (locationPermissionState.permissions.any { it.status.isGranted }) {
                    viewModel.onHandleUserMessageDone()
                }

                if (locationPermissionState.shouldShowRationale) {
                    val snackbarText = stringResource(R.string.need_permission)
                    LaunchedEffect(snackbarHostState, viewModel, snackbarText) {
                        keyboardController?.hide()
                        snackbarHostState.showSnackbar(snackbarText)
                        viewModel.onHandleUserMessageDone()
                    }
                }

                locationPermissionState.launchMultiplePermissionRequest()
            }

            val snackbarText = userMessage.message?.asString()
            val actionLabel = userMessage.actionLabel?.let { stringResource(it) }

            LaunchedEffect(snackbarHostState, snackbarText, actionLabel) {
                snackbarText?.let {
                    keyboardController?.hide()

                    if (userMessage is UserMessage.DeletingLocation) {
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    }

                    val result = snackbarHostState.showSnackbar(
                        message = snackbarText,
                        actionLabel = actionLabel,
                        duration = SnackbarDuration.Short,
                    )

                    if (result == SnackbarResult.ActionPerformed) {
                        viewModel.onDeleteLocation()
                    }
                }

                viewModel.onHandleUserMessageDone()
            }
        }
    }
}

@Composable
fun WeatherSearchScreenContent(
    uiState: WeatherSearchState,
    onRefresh: () -> Unit,
    onCityClick: (CityUiModel) -> Unit,
    onCityLongClick: (SavedCity) -> Unit,
    onCurrentLocationFieldClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    when (val screenType = getSearchScreenType(uiState)) {
        SearchScreenType.NoResult -> {
            Text(
                stringResource(R.string.no_result),
                style = MaterialTheme.typography.titleMedium,
                textAlign = TextAlign.Center,
                modifier = modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
            )
        }

        SearchScreenType.SuggestionFeed -> {
            uiState as WeatherSearchState.ShowSuggestionCities
            LazyColumn(modifier.fillMaxWidth()) {
                items(uiState.suggestionCities) { city ->
                    SuggestionCityItem(city = city) { selectedCity ->
                        onCityClick(selectedCity)
                    }
                }
            }
        }

        else -> {
            uiState as WeatherSearchState.ShowSaveCities
            SavedCitiesContent(
                screenType = screenType,
                savedCities = uiState.savedCities,
                isLoading = uiState.isLoading,
                onRefresh = onRefresh,
                onCityClick = onCityClick,
                onCityLongClick = onCityLongClick,
                onCurrentLocationFieldClick = onCurrentLocationFieldClick,
                modifier = modifier,
            )
        }
    }
}

@Composable
private fun SavedCitiesContent(
    screenType: SearchScreenType,
    savedCities: List<SavedCity>,
    isLoading: Boolean,
    onRefresh: () -> Unit,
    onCityClick: (CityUiModel) -> Unit,
    onCityLongClick: (SavedCity) -> Unit,
    onCurrentLocationFieldClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    LoadingContent(
        isLoading = isLoading,
        onRefresh = onRefresh,
        modifier = modifier,
    ) {
        LazyColumn(Modifier.fillMaxWidth()) {
            if (screenType == SearchScreenType.SavedInfoFeedWithYourLocationButton) {
                item { CurrentLocationField(onCurrentLocationFieldClick) }
            }
            items(savedCities) { city ->
                SavedCityItem(
                    city = city,
                    onCityClick = { onCityClick(city) },
                    onCityLongClick = { onCityLongClick(city) },
                )
            }
        }
    }
}

private fun getSearchScreenType(uiState: WeatherSearchState): SearchScreenType {
    return when (uiState) {
        is WeatherSearchState.ShowSaveCities -> {
            if (uiState.isLoading || uiState.savedCities.any { it.isCurrentLocation }) {
                return SearchScreenType.SavedInfoFeed
            }

            SearchScreenType.SavedInfoFeedWithYourLocationButton
        }

        is WeatherSearchState.ShowSuggestionCities -> {
            if (uiState.suggestionCities.isEmpty()) {
                SearchScreenType.NoResult
            } else {
                SearchScreenType.SuggestionFeed
            }
        }
    }
}
