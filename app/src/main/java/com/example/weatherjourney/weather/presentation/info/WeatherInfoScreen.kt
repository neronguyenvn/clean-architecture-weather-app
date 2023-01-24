package com.example.weatherjourney.weather.presentation.info

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.findViewTreeViewModelStoreOwner
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.weatherjourney.R
import com.example.weatherjourney.presentation.theme.Black70
import com.example.weatherjourney.presentation.theme.superscript
import com.example.weatherjourney.util.LoadingContent
import com.example.weatherjourney.util.UiEvent
import com.example.weatherjourney.weather.domain.model.CurrentWeather
import com.example.weatherjourney.weather.domain.model.DailyWeather
import com.example.weatherjourney.weather.domain.model.HourlyWeather
import com.example.weatherjourney.weather.presentation.info.component.InfoTopBar

@Composable
fun WeatherInfoScreen(
    onSearchClick: () -> Unit,
    onSettingClick: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: WeatherInfoViewModel = LocalView.current.findViewTreeViewModelStoreOwner()
        .let { hiltViewModel(it!!) }
) {
    val uiState = viewModel.uiState
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            InfoTopBar(
                city = uiState.city,
                onSearchClick = onSearchClick,
                onSettingClick = onSettingClick
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->

        WeatherInfoScreenContent(
            isLoading = uiState.isLoading,
            onRefresh = { viewModel.onEvent(WeatherInfoEvent.OnRefresh) },
            current = uiState.weatherState.current,
            listDaily = uiState.weatherState.listDaily,
            listHourly = uiState.weatherState.listHourly,
            modifier = Modifier.padding(paddingValues)
        )

        LaunchedEffect(true) {
            viewModel.uiEvent.collect { event ->
                when (event) {
                    is UiEvent.ShowSnackbar -> snackbarHostState.showSnackbar(
                        event.message.asString(context)
                    )
                }
            }
        }
    }
}

@Composable
fun WeatherInfoScreenContent(
    isLoading: Boolean,
    onRefresh: () -> Unit,
    current: CurrentWeather?,
    listDaily: List<DailyWeather>,
    listHourly: List<HourlyWeather>,
    modifier: Modifier = Modifier
) {
    val screenPadding = PaddingValues(
        start = dimensionResource(R.dimen.horizontal_margin),
        end = dimensionResource(R.dimen.horizontal_margin),
        top = dimensionResource(R.dimen.vertical_margin)
    )

    LoadingContent(isLoading, onRefresh, modifier) {
        LazyColumn(
            Modifier.fillMaxWidth(),
            contentPadding = screenPadding
        ) {
            item { CurrentWeatherContent(current) }
            item { Spacer(Modifier.height(32.dp)) }
            item { DailyWeatherContent(listDaily) }
            item { Spacer(Modifier.height(32.dp)) }
            item { CurrentDetailContent(current) }
            item { Spacer(Modifier.height(32.dp)) }
            items(listHourly) { hourly ->
                HourlyWeatherItem(hourly)
            }
        }
    }
}

@Composable
fun CurrentWeatherContent(
    current: CurrentWeather?,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        current?.let {
            Text(current.date, style = MaterialTheme.typography.labelMedium)
            Text(
                style = MaterialTheme.typography.displayLarge,
                text = buildAnnotatedString {
                    append(stringResource(R.string.number, current.temp))
                    withStyle(superscript) {
                        append(stringResource(R.string.celsius_symbol))
                    }
                }
            )

            Text(current.weather, style = MaterialTheme.typography.titleLarge)
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
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = modifier
            .border(1.dp, Color.Black, RoundedCornerShape(20.dp))
            .padding(16.dp)
    ) {
        Text(
            text = stringResource(R.string.daily_weather, daily.date, daily.weather),
            style = MaterialTheme.typography.titleSmall,
            textAlign = TextAlign.Center
        )
        AsyncImage(
            model = ImageRequest.Builder(context = LocalContext.current)
                .data(daily.imageUrl)
                .crossfade(true)
                .build(),
            placeholder = painterResource(R.drawable.ic_loading),
            error = painterResource(R.drawable.ic_broken_image),
            contentDescription = null
        )
        Text(
            text = stringResource(
                R.string.max_min_temperature,
                daily.maxTemp,
                daily.minTemp
            ),
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

@Composable
fun CurrentDetailContent(
    current: CurrentWeather?,
    modifier: Modifier = Modifier
) {
    current?.let {
        Row(
            modifier = modifier
                .fillMaxWidth()
                .border(1.dp, Color.Black, RoundedCornerShape(20.dp))
                .padding(16.dp)
        ) {
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                DetailItem(
                    stringResource(R.string.real_feel),
                    stringResource(R.string.temperature, current.realFeelTemp)
                )
                DetailItem(
                    stringResource(R.string.chance_of_rain),
                    stringResource(R.string.percent, current.rainChance)
                )
                DetailItem(
                    stringResource(R.string.visibility),
                    stringResource(R.string.meter, current.visibility)
                )
            }
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                DetailItem(
                    stringResource(R.string.humidity),
                    stringResource(R.string.percent, current.humidity)
                )
                DetailItem(
                    stringResource(R.string.pressure),
                    stringResource(R.string.hpa, current.pressure)
                )
                DetailItem(
                    stringResource(R.string.uv_index),
                    stringResource(R.string.number, current.uvIndex)
                )
            }
        }
    }
}

@Composable
fun DetailItem(
    label: String,
    detail: String,
    modifier: Modifier = Modifier
) {
    Column(modifier) {
        Text(label, style = MaterialTheme.typography.labelMedium, color = Black70)
        Text(detail, style = MaterialTheme.typography.bodyMedium)
    }
}

@Composable
fun HourlyWeatherItem(hourly: HourlyWeather, modifier: Modifier = Modifier) {
    Row(
        modifier.padding(start = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(hourly.date, style = MaterialTheme.typography.bodyMedium)
        AsyncImage(
            model = ImageRequest.Builder(context = LocalContext.current)
                .data(hourly.imageUrl)
                .crossfade(true)
                .build(),
            placeholder = painterResource(R.drawable.ic_loading),
            error = painterResource(R.drawable.ic_broken_image),
            contentDescription = null
        )
        Text(
            stringResource(R.string.temperature, hourly.temp),
            style = MaterialTheme.typography.bodyMedium
        )
        Text(
            stringResource(R.string.meter_per_second, hourly.windSpeed),
            style = MaterialTheme.typography.bodyMedium
        )
    }
}
