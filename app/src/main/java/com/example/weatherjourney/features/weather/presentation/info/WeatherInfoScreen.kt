package com.example.weatherjourney.features.weather.presentation.info

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.Card
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
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
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.findViewTreeViewModelStoreOwner
import com.example.weatherjourney.R
import com.example.weatherjourney.features.weather.domain.model.Coordinate
import com.example.weatherjourney.features.weather.domain.model.unit.AllUnit
import com.example.weatherjourney.features.weather.domain.model.weather.CurrentWeather
import com.example.weatherjourney.features.weather.domain.model.weather.DailyWeather
import com.example.weatherjourney.features.weather.presentation.info.component.DailyWeatherItem
import com.example.weatherjourney.features.weather.presentation.info.component.HourlyWeatherItem
import com.example.weatherjourney.features.weather.presentation.info.component.InfoTopBar
import com.example.weatherjourney.features.weather.presentation.info.component.WeatherDataDisplay
import com.example.weatherjourney.presentation.NAVIGATE_FROM_SEARCH
import com.example.weatherjourney.presentation.component.LoadingContent
import com.example.weatherjourney.presentation.theme.superscript
import com.example.weatherjourney.util.UserMessage.AddingLocation
import com.example.weatherjourney.util.roundTo
import kotlin.math.roundToInt

private const val TAG = "WeatherInfoScreen"

@Composable
fun WeatherInfoScreen(
    city: String,
    coordinate: Coordinate,
    timeZone: String,
    snackbarHostState: SnackbarHostState,
    navigationKey: Int,
    countryCode: String,
    onSearchClick: () -> Unit,
    onSettingClick: () -> Unit,
    onNotificationClick: () -> Unit,
    onNavigationToInfoDone: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: WeatherInfoViewModel = LocalView.current.findViewTreeViewModelStoreOwner()
        .let { hiltViewModel(it!!) }
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    Log.d(TAG, "UiState flow collected: $uiState")

    Scaffold(
        modifier = modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            InfoTopBar(
                cityAddress = uiState.allWeather.cityAddress,
                onSearchClick = {
                    viewModel.onClearListenJob()
                    onSearchClick()
                },
                onSettingClick = {
                    viewModel.onClearListenJob()
                    onSettingClick()
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton({
                viewModel.onClearListenJob()
                onNotificationClick()
            }) {
                Icon(
                    Icons.Outlined.Notifications,
                    contentDescription = stringResource(R.string.notification)
                )
            }
        }
    ) { paddingValues ->

        WeatherInfoScreenContent(
            uiState = uiState,
            onRefresh = { viewModel.onRefresh() },
            modifier = Modifier.padding(paddingValues)
        )

        val currentOnNavigationDone by rememberUpdatedState(onNavigationToInfoDone)
        LaunchedEffect(currentOnNavigationDone) {
            if (navigationKey == NAVIGATE_FROM_SEARCH) {
                viewModel.onNavigateFromSearch(city, coordinate, timeZone)
                currentOnNavigationDone()
            }
        }

        uiState.userMessage?.let { userMessage ->
            val snackbarText = userMessage.message?.asString()
            val actionLabel = userMessage.actionLabel?.let { stringResource(it) }

            LaunchedEffect(snackbarHostState, snackbarText, actionLabel) {
                snackbarText?.let {
                    val result = snackbarHostState.showSnackbar(
                        message = it,
                        actionLabel = actionLabel,
                        duration = SnackbarDuration.Short
                    )

                    if (result == SnackbarResult.ActionPerformed && userMessage is AddingLocation) {
                        viewModel.onSaveInfo(countryCode)
                    }

                    viewModel.onHandleUserMessageDone()
                }
            }
        }
    }
}

@Composable
fun WeatherInfoScreenContent(
    uiState: WeatherInfoUiState,
    onRefresh: () -> Unit,
    modifier: Modifier = Modifier
) {
    val screenPadding = PaddingValues(
        start = dimensionResource(R.dimen.horizontal_margin),
        end = dimensionResource(R.dimen.horizontal_margin),
        top = dimensionResource(R.dimen.vertical_margin)
    )

    LoadingContent(uiState.isLoading, onRefresh, modifier) {
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .padding(screenPadding)
        ) {
            item {
                CurrentWeatherContent(
                    uiState.allWeather.current,
                    uiState.allUnit,
                    uiState.isCurrentLocation
                )
            }
            item { Spacer(Modifier.height(32.dp)) }
            item { DailyWeatherContent(uiState.allWeather.listDaily) }
            item { Spacer(Modifier.height(32.dp)) }
            items(uiState.allWeather.listHourly) { hourly ->
                HourlyWeatherItem(hourly, uiState.allUnit?.windSpeed)
            }
        }
    }
}

@Composable
fun CurrentWeatherContent(
    current: CurrentWeather?,
    allUnit: AllUnit?,
    isCurrentLocation: Boolean,
    modifier: Modifier = Modifier
) {
    current?.let {
        allUnit?.let { units ->
            Card(modifier) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row {
                        if (isCurrentLocation) {
                            Text(
                                stringResource(R.string.your_location),
                                style = MaterialTheme.typography.labelMedium,
                                color = Color.White
                            )
                        }
                        Spacer(Modifier.weight(1f))
                        Text(
                            current.date,
                            style = MaterialTheme.typography.labelMedium,
                            color = Color.White
                        )
                    }
                    Spacer(modifier = Modifier.height(20.dp))
                    Image(
                        painter = painterResource(current.weatherType.iconRes),
                        contentDescription = null,
                        modifier = Modifier.height(150.dp)
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        style = MaterialTheme.typography.displayLarge,
                        color = Color.White,
                        text = buildAnnotatedString {
                            append("${current.temp.roundTo(1)}")
                            withStyle(superscript) {
                                append(units.temperature.label)
                            }
                        }
                    )
                    Text(
                        current.weatherType.weatherDesc,
                        style = MaterialTheme.typography.titleLarge,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.height(32.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        WeatherDataDisplay(
                            value = current.pressure.roundTo(1),
                            unit = units.pressure.label,
                            icon = ImageVector.vectorResource(R.drawable.ic_pressure),
                            iconTint = Color.White,
                            textStyle = TextStyle(color = Color.White)
                        )
                        WeatherDataDisplay(
                            value = current.humidity.roundToInt(),
                            unit = "%",
                            icon = ImageVector.vectorResource(R.drawable.ic_drop),
                            iconTint = Color.White,
                            textStyle = TextStyle(color = Color.White)
                        )
                        WeatherDataDisplay(
                            value = current.windSpeed.roundTo(1),
                            unit = units.windSpeed.label,
                            icon = ImageVector.vectorResource(R.drawable.ic_wind),
                            iconTint = Color.White,
                            textStyle = TextStyle(color = Color.White)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun DailyWeatherContent(listDaily: List<DailyWeather>, modifier: Modifier = Modifier) {
    LazyRow(modifier, horizontalArrangement = Arrangement.spacedBy(16.dp)) {
        items(listDaily) { daily ->
            DailyWeatherItem(daily)
        }
    }
}
