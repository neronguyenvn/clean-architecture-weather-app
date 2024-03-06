package com.example.weatherjourney.features.weather.info

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Card
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.weatherjourney.R
import com.example.weatherjourney.core.common.util.roundTo
import com.example.weatherjourney.core.designsystem.component.PullToLoadContent
import com.example.weatherjourney.core.model.unit.AllUnit
import com.example.weatherjourney.core.model.unit.WindSpeedUnit
import com.example.weatherjourney.core.model.weather.CurrentWeather
import com.example.weatherjourney.core.model.weather.DailyWeather
import com.example.weatherjourney.core.model.weather.HourlyWeather
import com.example.weatherjourney.presentation.theme.superscript
import kotlin.math.roundToInt

sealed class WeatherInfoEvent {
    data object Refresh : WeatherInfoEvent()
}

@Composable
fun WeatherInfoScreen(
    snackbarHostState: SnackbarHostState,
    onSearchClick: () -> Unit,
    onSettingClick: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: WeatherInfoViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        modifier = modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            InfoTopBar(
                cityAddress = uiState.cityAddress,
                onSearchClick = onSearchClick,
                onSettingClick = onSettingClick,
                isCurrentLocation = uiState.isCurrentLocation
            )
        },
    ) { paddingValues ->

        WeatherInfoUi(
            uiState = uiState,
            modifier = Modifier.padding(paddingValues),
        )
    }
}

@Composable
fun WeatherInfoUi(
    uiState: WeatherInfoUiState,
    modifier: Modifier = Modifier,
) {
    val screenPadding = PaddingValues(
        start = dimensionResource(R.dimen.horizontal_margin),
        end = dimensionResource(R.dimen.horizontal_margin),
        top = dimensionResource(R.dimen.vertical_margin),
    )

    PullToLoadContent(
        isLoading = uiState.isLoading,
        modifier = modifier,
        onRefresh = { uiState.eventSink(WeatherInfoEvent.Refresh) }
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .padding(screenPadding),
        ) {
            uiState.weather?.current?.let {
                item {
                    CurrentWeatherContent(
                        uiState.weather.current,
                        uiState.units,
                        uiState.isCurrentLocation,
                    )
                }
            }
            item { Spacer(Modifier.height(32.dp)) }
            uiState.weather?.let {
                item { DailyWeatherContent(uiState.weather.listDaily) }
                item { Spacer(Modifier.height(32.dp)) }
                items(uiState.weather.listHourly) { hourly ->
                    HourlyWeatherItem(hourly, uiState.units?.windSpeed)
                }
            }
        }
    }
}

@Suppress("LongMethod")
@Composable
fun CurrentWeatherContent(
    current: CurrentWeather?,
    allUnit: AllUnit?,
    isCurrentLocation: Boolean,
    modifier: Modifier = Modifier,
) {
    current?.let {
        allUnit?.let { units ->
            Card(modifier) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Row {
                        if (isCurrentLocation) {
                            Text(
                                stringResource(R.string.your_location),
                                style = MaterialTheme.typography.labelMedium,
                                color = Color.White,
                            )
                        }
                        Spacer(Modifier.weight(1f))
                        Text(
                            current.date,
                            style = MaterialTheme.typography.labelMedium,
                            color = Color.White,
                        )
                    }
                    Spacer(modifier = Modifier.height(20.dp))
                    Image(
                        painter = painterResource(current.weatherType.iconRes),
                        contentDescription = null,
                        modifier = Modifier.height(150.dp),
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
                        },
                    )
                    Text(
                        current.weatherType.weatherDesc,
                        style = MaterialTheme.typography.titleLarge,
                        color = Color.White,
                    )
                    Spacer(modifier = Modifier.height(32.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround,
                    ) {
                        WeatherDataDisplay(
                            value = current.pressure.roundTo(1),
                            unit = units.pressure.label,
                            icon = ImageVector.vectorResource(R.drawable.ic_pressure),
                            iconTint = Color.White,
                            textStyle = TextStyle(color = Color.White),
                        )
                        WeatherDataDisplay(
                            value = current.humidity.roundToInt(),
                            unit = "%",
                            icon = ImageVector.vectorResource(R.drawable.ic_drop),
                            iconTint = Color.White,
                            textStyle = TextStyle(color = Color.White),
                        )
                        WeatherDataDisplay(
                            value = current.windSpeed.roundTo(1),
                            unit = units.windSpeed.label,
                            icon = ImageVector.vectorResource(R.drawable.ic_wind),
                            iconTint = Color.White,
                            textStyle = TextStyle(color = Color.White),
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

@Composable
fun DailyWeatherItem(
    daily: DailyWeather,
    modifier: Modifier = Modifier,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween,
        modifier = modifier
            .border(1.dp, MaterialTheme.colorScheme.onBackground, RoundedCornerShape(20.dp))
            .padding(16.dp)
            .height(150.dp)
            .width(100.dp),
    ) {
        val context = LocalContext.current

        Text(
            text = "${daily.date.asString(context)}\n${daily.weatherType.weatherDesc}",
            style = MaterialTheme.typography.titleSmall,
            textAlign = TextAlign.Center,
        )
        Image(
            painter = painterResource(daily.weatherType.iconRes),
            contentDescription = null,
            modifier = Modifier.width(40.dp),
        )
        Text(
            text = stringResource(
                R.string.max_min_temperature,
                daily.maxTemp.roundToInt(),
                daily.minTemp.roundToInt(),
            ),
            style = MaterialTheme.typography.bodyMedium,
        )
    }
}

@Composable
fun HourlyWeatherItem(
    hourly: HourlyWeather,
    windSpeedUnit: WindSpeedUnit?,
    modifier: Modifier = Modifier,
) {
    windSpeedUnit?.let {
        Row(
            modifier = modifier
                .height(50.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Text(
                hourly.date,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.weight(1f),
            )
            Image(
                painter = painterResource(hourly.weatherType.iconRes),
                contentDescription = null,
                contentScale = ContentScale.FillWidth,
                modifier = Modifier.width(40.dp),
            )
            Text(
                stringResource(R.string.temperature, hourly.temp.roundTo(1)),
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                modifier = Modifier.weight(0.6f),
            )
            Text(
                "${hourly.windSpeed.roundTo(1)}${windSpeedUnit.label}",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.weight(2f),
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InfoTopBar(
    cityAddress: String,
    isCurrentLocation: Boolean,
    onSearchClick: () -> Unit,
    onSettingClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    CenterAlignedTopAppBar(
        modifier = modifier,
        navigationIcon = {
            IconButton(onSearchClick) {
                Icon(
                    painter = painterResource(R.drawable.ic_search),
                    contentDescription = stringResource(R.string.search),
                )
            }
        },
        title = {
            Row {
                if (isCurrentLocation) {
                    Icon(Icons.Default.LocationOn, "current location")
                    Spacer(Modifier.width(4.dp))
                }
                Text(
                    cityAddress,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 1,
                    textAlign = TextAlign.Center,
                )
            }
        },
        actions = {
            IconButton(onSettingClick) {
                Icon(
                    painter = painterResource(R.drawable.ic_setting),
                    contentDescription = stringResource(R.string.setting),
                    tint = MaterialTheme.colorScheme.onSurface,
                )
            }
        },
    )
}

@Composable
fun WeatherDataDisplay(
    value: Number,
    unit: String,
    icon: ImageVector,
    modifier: Modifier = Modifier,
    textStyle: TextStyle = TextStyle(),
    iconTint: Color = Color.White,
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = iconTint,
            modifier = Modifier.size(25.dp),
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = "$value$unit",
            style = textStyle,
        )
    }
}
