package com.example.weatherjourney.weather.presentation.info

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.findViewTreeViewModelStoreOwner
import com.example.weatherjourney.R
import com.example.weatherjourney.presentation.component.LoadingContent
import com.example.weatherjourney.presentation.theme.superscript
import com.example.weatherjourney.util.UiEvent
import com.example.weatherjourney.weather.domain.model.Coordinate
import com.example.weatherjourney.weather.domain.model.CurrentWeather
import com.example.weatherjourney.weather.domain.model.DailyWeather
import com.example.weatherjourney.weather.presentation.info.component.DailyWeatherItem
import com.example.weatherjourney.weather.presentation.info.component.HourlyWeatherItem
import com.example.weatherjourney.weather.presentation.info.component.InfoTopBar
import com.example.weatherjourney.weather.presentation.info.component.WeatherDataDisplay
import kotlin.math.roundToInt

@Composable
fun WeatherInfoScreen(
    city: String,
    coordinate: Coordinate,
    timeZone: String,
    snackbarHostState: SnackbarHostState,
    onSearchClick: () -> Unit,
    onSettingClick: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: WeatherInfoViewModel = LocalView.current.findViewTreeViewModelStoreOwner()
        .let { hiltViewModel(it!!) }
) {
    val uiState = viewModel.uiState
    val context = LocalContext.current

    Scaffold(
        modifier = modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            InfoTopBar(
                cityAddress = uiState.cityAddress,
                onSearchClick = onSearchClick,
                onSettingClick = onSettingClick
            )
        }
    ) { paddingValues ->

        WeatherInfoScreenContent(
            uiState = uiState,
            onRefresh = { viewModel.onEvent(WeatherInfoEvent.OnRefresh) },
            modifier = Modifier.padding(paddingValues)
        )

        LaunchedEffect(true) {
            if (city.isNotBlank()) {
                viewModel.onEvent(
                    WeatherInfoEvent.OnFetchWeatherFromSearch(
                        city,
                        coordinate,
                        timeZone
                    )
                )
            }

            viewModel.uiEvent.collect { event ->
                when (event) {
                    is UiEvent.ShowSnackbar -> {
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
                                R.string.add -> viewModel.onEvent(WeatherInfoEvent.OnCacheInfo)
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
            Modifier.fillMaxWidth(),
            contentPadding = screenPadding
        ) {
            item { CurrentWeatherContent(uiState.weatherState.current, uiState.temperatureLabel) }
            item { Spacer(Modifier.height(32.dp)) }
            item { DailyWeatherContent(uiState.weatherState.listDaily) }
            item { Spacer(Modifier.height(32.dp)) }
            items(uiState.weatherState.listHourly) { hourly ->
                HourlyWeatherItem(hourly)
            }
        }
    }
}

@Composable
fun CurrentWeatherContent(
    current: CurrentWeather?,
    temperatureLabel: String,
    modifier: Modifier = Modifier
) {
    current?.let {
        Card(
            shape = RoundedCornerShape(10.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            modifier = modifier
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    current.date,
                    style = MaterialTheme.typography.labelMedium,
                    modifier = Modifier.align(Alignment.End),
                    color = Color.White
                )
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
                        append("${current.temp}")
                        withStyle(superscript) {
                            append(temperatureLabel)
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
                        value = current.pressure.roundToInt(),
                        unit = "hpa",
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
                        value = current.windSpeed.roundToInt(),
                        unit = "km/h",
                        icon = ImageVector.vectorResource(R.drawable.ic_wind),
                        iconTint = Color.White,
                        textStyle = TextStyle(color = Color.White)
                    )
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
