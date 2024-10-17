package com.example.weatherjourney.feature.search

import android.annotation.SuppressLint
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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
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
import com.example.weatherjourney.core.common.util.roundTo
import com.example.weatherjourney.core.designsystem.component.AddressWithFlag
import com.example.weatherjourney.core.designsystem.component.CurrentLocationField
import com.example.weatherjourney.core.designsystem.component.HorizontalDivider
import com.example.weatherjourney.core.model.search.Location
import com.example.weatherjourney.core.model.search.LocationWithWeather
import com.example.weatherjourney.feature.search.WeatherSearchEvent.ClickOnSavedLocation
import com.example.weatherjourney.feature.search.WeatherSearchEvent.ClickOnSuggestionLocation
import com.example.weatherjourney.feature.search.WeatherSearchEvent.InputLocation
import com.example.weatherjourney.feature.search.WeatherSearchEvent.LongClickOnSavedLocation
import com.example.weatherjourney.feature.search.WeatherSearchEvent.Refresh
import com.example.weatherjourney.feature.search.WeatherSearchUiState.NoResult
import com.example.weatherjourney.feature.search.WeatherSearchUiState.SavedLocationsFeed
import com.example.weatherjourney.feature.search.WeatherSearchUiState.SuggestionLocationsFeed
import com.example.weatherjourney.presentation.theme.White70


sealed interface WeatherSearchEvent {

    data object Refresh : WeatherSearchEvent

    data class InputLocation(val value: String) : WeatherSearchEvent

    data class ClickOnSuggestionLocation(val location: Location) : WeatherSearchEvent

    data class ClickOnSavedLocation(val location: LocationWithWeather) : WeatherSearchEvent

    data class LongClickOnSavedLocation(val location: LocationWithWeather?) : WeatherSearchEvent

    data class DeleteSavedLocation(val location: LocationWithWeather) : WeatherSearchEvent
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun WeatherSearchScreen(
    snackbarHostState: SnackbarHostState,
    onBackClick: () -> Unit,
    navigateToInfo: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: WeatherSearchViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    LaunchedEffect(true) {
        uiState.eventSink(Refresh)
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            SearchBar(
                value = when (uiState) {
                    is NoResult -> (uiState as NoResult).input
                    is SuggestionLocationsFeed -> (uiState as SuggestionLocationsFeed).input
                    else -> ""
                },
                onBackClick = onBackClick,
                onValueChange = { uiState.eventSink(InputLocation(it)) },
                onValueClear = { uiState.eventSink(InputLocation("")) },
                modifier = Modifier.padding(top = 8.dp),
            )
        },
    ) {
        WeatherSearchUi(
            uiState = uiState,
            snackbarHostState = snackbarHostState,
            navigateToInfo = navigateToInfo
        )
    }
}

/*        val activityResultLauncher = rememberLauncherForActivityResult(
    ActivityResultContracts.StartIntentSenderForResult(),
) { result ->
    viewModel.onPermissionActionResult(result.resultCode == Activity.RESULT_OK, true)
}

val locationPermissionState = rememberMultiplePermissionsState(
    listOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION,
    ),
) { viewModel.onPermissionActionResult(it.any { permission -> permission.value }) }*/

/*        uiState.userMessage?.let { userMessage ->
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
            }*/

@Composable
fun WeatherSearchUi(
    uiState: WeatherSearchUiState,
    snackbarHostState: SnackbarHostState,
    navigateToInfo: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val keyboardController = LocalSoftwareKeyboardController.current
    val haptic = LocalHapticFeedback.current

    when (uiState) {
        is NoResult -> Text(
            stringResource(R.string.no_result),
            style = MaterialTheme.typography.titleMedium,
            textAlign = TextAlign.Center,
            modifier = modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
        )


        is SuggestionLocationsFeed -> LazyColumn(modifier.fillMaxWidth()) {
            items(
                uiState.locations
            ) { location ->
                SuggestionLocationItem(location) { selected ->
                    uiState.eventSink(ClickOnSuggestionLocation(selected))
                    navigateToInfo()
                }
            }
        }

        is SavedLocationsFeed -> {
            SavedLocationsUi(
                needLocateButton = uiState.hasLocateButton,
                locations = uiState.locationWithWeathers,
                isLoading = uiState.isLoading,
                onRefresh = { uiState.eventSink(Refresh) },
                onClick = {
                    uiState.eventSink(ClickOnSavedLocation(it))
                    navigateToInfo()
                },
                onLongClick = { uiState.eventSink(LongClickOnSavedLocation(it)) },
                modifier = modifier,
            )

            val selected = uiState.selectedLocation
            LaunchedEffect(selected != null) {
                selected?.let {
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    keyboardController?.hide()

                    when (snackbarHostState.showSnackbar(
                        message = context.getString(R.string.delete_location, it.address),
                        actionLabel = context.getString(R.string.delete),
                        duration = SnackbarDuration.Short,
                    )) {
                        SnackbarResult.ActionPerformed ->
                            uiState.eventSink(WeatherSearchEvent.DeleteSavedLocation(selected))

                        SnackbarResult.Dismissed -> uiState.eventSink(
                            LongClickOnSavedLocation(null)
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SavedLocationsUi(
    needLocateButton: Boolean,
    locations: List<LocationWithWeather?>,
    isLoading: Boolean,
    onRefresh: () -> Unit,
    onClick: (LocationWithWeather) -> Unit,
    onLongClick: (LocationWithWeather) -> Unit,
    modifier: Modifier = Modifier,
    onCurrentLocationFieldClick: () -> Unit = {},
) {
    PullToRefreshBox(
        isRefreshing = isLoading,
        onRefresh = onRefresh,
        modifier = modifier,
    ) {
        LazyColumn(Modifier.fillMaxWidth()) {
            if (needLocateButton) {
                item { CurrentLocationField(onCurrentLocationFieldClick) }
            }
            items(locations) { location ->
                SavedLocationItem(
                    location = location,
                    onClick = { onClick(location!!) },
                    onLongClick = { onLongClick(location!!) },
                )
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun SavedLocationItem(
    location: LocationWithWeather?,
    onClick: (LocationWithWeather) -> Unit,
    onLongClick: (LocationWithWeather) -> Unit,
    modifier: Modifier = Modifier,
) {
    if (location == null) {
        Spacer(
            modifier = Modifier
                .height(32.dp)
                .fillMaxWidth()
        )
    } else {
        Column(
            modifier = modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .combinedClickable(
                    onClick = { onClick(location) },
                    onLongClick = { onLongClick(location) },
                ),
        ) {
            Spacer(Modifier.height(16.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
            ) {
                if (location.isCurrentLocation) {
                    Column(modifier = Modifier.weight(1f)) {
                        AddressWithFlag(
                            countryCode = location.countryCode,
                            address = location.address,
                        )
                        Text(
                            text = stringResource(R.string.your_location),
                            style = MaterialTheme.typography.labelMedium.copy(White70),
                        )
                    }
                } else {
                    AddressWithFlag(
                        countryCode = location.countryCode,
                        address = location.address,
                        modifier = Modifier.weight(1f),
                    )
                }
                Spacer(Modifier.width(8.dp))
                Text(
                    stringResource(R.string.temperature, location.temp.roundTo(1)),
                    style = MaterialTheme.typography.bodyLarge,
                )
                Spacer(Modifier.width(8.dp))
                Image(
                    painter = painterResource(location.weatherType.iconRes),
                    contentDescription = null,
                    modifier = Modifier.width(30.dp),
                )
            }
            Spacer(Modifier.height(16.dp))
            HorizontalDivider()
        }
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
            Box(Modifier.weight(1f)) {
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
                    Icon(Icons.Filled.Close, "Delete input")
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
fun SuggestionLocationItem(
    location: Location,
    modifier: Modifier = Modifier,
    onClick: (Location) -> Unit,
) {
    Column(
        modifier = modifier
            .clickable { onClick(location) }
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
    ) {
        Spacer(Modifier.height(16.dp))
        AddressWithFlag(countryCode = location.countryCode, address = location.address)
        Spacer(Modifier.height(16.dp))
        HorizontalDivider()
    }
}
