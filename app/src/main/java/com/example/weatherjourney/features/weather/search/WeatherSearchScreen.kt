package com.example.weatherjourney.features.weather.search

import android.Manifest
import android.app.Activity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
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
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.weatherjourney.R
import com.example.weatherjourney.core.common.util.UserMessage
import com.example.weatherjourney.core.common.util.UserMessage.RequestingTurnOnLocationService
import com.example.weatherjourney.core.common.util.roundTo
import com.example.weatherjourney.core.designsystem.component.CityAddressWithFlag
import com.example.weatherjourney.core.designsystem.component.CurrentLocationField
import com.example.weatherjourney.core.designsystem.component.HorizontalDivider
import com.example.weatherjourney.core.designsystem.component.LoadingContent
import com.example.weatherjourney.core.model.location.CityUiModel
import com.example.weatherjourney.core.model.location.CityWithWeather
import com.example.weatherjourney.core.model.location.SuggestionCity
import com.example.weatherjourney.presentation.theme.White70
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
            onRefresh = {},
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
                    //      viewModel.onHandleUserMessageDone()
                }

                if (locationPermissionState.shouldShowRationale) {
                    val snackbarText = stringResource(R.string.need_permission)
                    LaunchedEffect(snackbarHostState, viewModel, snackbarText) {
                        keyboardController?.hide()
                        snackbarHostState.showSnackbar(snackbarText)
                        //        viewModel.onHandleUserMessageDone()
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

                //              viewModel.onHandleUserMessageDone()
            }
        }
    }
}

@Composable
fun WeatherSearchScreenContent(
    uiState: WeatherSearchState,
    onRefresh: () -> Unit,
    onCityClick: (CityUiModel) -> Unit,
    onCityLongClick: (CityWithWeather) -> Unit,
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
    savedCities: List<CityWithWeather>,
    isLoading: Boolean,
    onRefresh: () -> Unit,
    onCityClick: (CityUiModel) -> Unit,
    onCityLongClick: (CityWithWeather) -> Unit,
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


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun SavedCityItem(
    city: CityWithWeather,
    onCityClick: (CityWithWeather) -> Unit,
    onCityLongClick: (CityWithWeather) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .combinedClickable(
                onClick = { onCityClick(city) },
                onLongClick = { onCityLongClick(city) },
            ),
    ) {
        Spacer(Modifier.height(16.dp))
        Row(
            verticalAlignment = Alignment.CenterVertically,
        ) {
            if (city.isCurrentLocation) {
                Column(modifier = Modifier.weight(1f)) {
                    CityAddressWithFlag(
                        countryCode = city.countryCode,
                        cityAddress = city.cityAddress,
                    )
                    Text(
                        text = stringResource(R.string.your_location),
                        style = MaterialTheme.typography.labelMedium.copy(White70),
                    )
                }
            } else {
                CityAddressWithFlag(
                    countryCode = city.countryCode,
                    cityAddress = city.cityAddress,
                    modifier = Modifier.weight(1f),
                )
            }
            Spacer(Modifier.width(8.dp))
            Text(
                stringResource(R.string.temperature, city.temp.roundTo(1)),
                style = MaterialTheme.typography.bodyLarge,
            )
            Spacer(Modifier.width(8.dp))
            Image(
                painter = painterResource(city.weatherType.iconRes),
                contentDescription = null,
                modifier = Modifier.width(30.dp),
            )
        }
        Spacer(Modifier.height(16.dp))
        HorizontalDivider()
    }
}

@Composable
fun SearchBar(
    value: String,
    onBackClick: () -> Unit,
    onValueChange: (String) -> Unit,
    onValueClear: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val focusRequester = remember { FocusRequester() }
    val keyboardController = LocalSoftwareKeyboardController.current

    Column(modifier) {
        Row(
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            IconButton(onBackClick) {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = stringResource(R.string.back),
                )
            }
            Box(Modifier.weight(1.0f)) {
                if (value.isBlank()) {
                    Text(
                        color = White70,
                        text = stringResource(R.string.enter_location),
                    )
                }
                BasicTextField(
                    value = value,
                    onValueChange = onValueChange,
                    textStyle = MaterialTheme.typography.bodyLarge.copy(color = Color.White),
                    singleLine = true,
                    keyboardActions = KeyboardActions(onDone = { keyboardController?.hide() }),
                    cursorBrush = SolidColor(Color.White),
                    modifier = Modifier.focusRequester(focusRequester),
                )
            }
            if (value.isNotBlank()) {
                IconButton(onValueClear) {
                    Icon(
                        Icons.Filled.Close,
                        contentDescription = stringResource(R.string.delete_city_address_input),
                    )
                }
            }
        }
        HorizontalDivider()
    }

    LaunchedEffect(true) {
        focusRequester.requestFocus()
    }
}

@Composable
fun SuggestionCityItem(
    city: SuggestionCity,
    modifier: Modifier = Modifier,
    onCityClick: (SuggestionCity) -> Unit,
) {
    Column(
        modifier = modifier
            .clickable { onCityClick(city) }
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
    ) {
        Spacer(Modifier.height(16.dp))
        CityAddressWithFlag(countryCode = city.countryCode, cityAddress = city.cityAddress)
        Spacer(Modifier.height(16.dp))
        HorizontalDivider()
    }
}
