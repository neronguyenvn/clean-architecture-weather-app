package com.example.weatherjourney.feature.details

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
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
import com.example.weatherjourney.core.datastore.model.UserData
import com.example.weatherjourney.core.model.CurrentWeather
import com.example.weatherjourney.core.model.DailyWeather
import com.example.weatherjourney.core.model.HourlyWeather
import com.example.weatherjourney.core.model.LocationWithWeather
import com.example.weatherjourney.core.model.WindSpeedUnit
import com.example.weatherjourney.feature.details.DetailsUiState.*
import com.example.weatherjourney.presentation.theme.superscript
import kotlin.math.roundToInt

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun DetailsScreen(
    onBackClick: () -> Unit,
    onSettingsClick: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: DetailsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val locationWithWeather by viewModel.locationWithWeather.collectAsStateWithLifecycle()
    val userData by viewModel.userData.collectAsStateWithLifecycle()

    Column(modifier = modifier) {
        if (userData != null && locationWithWeather != null) {
            InfoTopBar(
                address = locationWithWeather!!.location.address,
                onBackClick = onBackClick,
                onSettingsClick = onSettingsClick,
            )
            WeatherInfoUi(
                uiState = uiState,
                locationWithWeather = locationWithWeather!!,
                userData = userData!!
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WeatherInfoUi(
    uiState: DetailsUiState,
    locationWithWeather: LocationWithWeather,
    userData: UserData,
    modifier: Modifier = Modifier,
) {
    val screenPadding = PaddingValues(
        start = 16.dp,
        end = 16.dp,
        top = 16.dp,
    )

    locationWithWeather.weather?.current?.let {
        PullToRefreshBox(
            isRefreshing = uiState is Loading,
            onRefresh = { }, // TODO
            modifier = modifier.padding(screenPadding),
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(32.dp)
            ) {
                item {
                    CurrentWeatherContent(
                        weather = locationWithWeather.weather.current,
                        userData = userData,
                    )
                }
                item {
                    DailyWeatherContent(locationWithWeather.weather.dailyForecasts)
                }
                items(locationWithWeather.weather.hourlyForecasts) { weather ->
                    HourlyWeatherItem(
                        weather = weather,
                        windSpeedUnit = userData.windSpeedUnit
                    )
                }
            }
        }
    }
}

@Composable
fun CurrentWeatherContent(
    weather: CurrentWeather,
    userData: UserData,
    modifier: Modifier = Modifier,
) {
    Card(modifier) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Row {
                Spacer(Modifier.weight(1f))
                Text(
                    weather.date,
                    style = MaterialTheme.typography.labelMedium,
                )
            }
            Spacer(modifier = Modifier.height(20.dp))
            Image(
                painter = painterResource(weather.weatherType.iconRes),
                contentDescription = null,
                modifier = Modifier.height(150.dp),
            )
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                style = MaterialTheme.typography.displayLarge,
                text = buildAnnotatedString {
                    append("${weather.temp.roundTo(1)}")
                    withStyle(superscript) {
                        append(userData.temperatureUnit.label)
                    }
                },
            )
            Text(
                weather.weatherType.weatherDesc,
                style = MaterialTheme.typography.titleLarge,
            )
            Spacer(modifier = Modifier.height(32.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround,
            ) {
                WeatherDataDisplay(
                    value = weather.pressure.roundTo(1),
                    unit = userData.pressureUnit.label,
                    icon = ImageVector.vectorResource(R.drawable.ic_pressure),
                )
                WeatherDataDisplay(
                    value = weather.humidity.roundToInt(),
                    unit = "%",
                    icon = ImageVector.vectorResource(R.drawable.ic_drop),
                )
                WeatherDataDisplay(
                    value = weather.windSpeed.roundTo(1),
                    unit = userData.windSpeedUnit.label,
                    icon = ImageVector.vectorResource(R.drawable.ic_wind),
                )
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
        Text(
            text = "${daily.date}\n${daily.weatherType.weatherDesc}",
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
    weather: HourlyWeather,
    windSpeedUnit: WindSpeedUnit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .height(50.dp)
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Text(
            weather.date,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.weight(1f),
        )
        Image(
            painter = painterResource(weather.weatherType.iconRes),
            contentDescription = null,
            contentScale = ContentScale.FillWidth,
            modifier = Modifier.width(40.dp),
        )
        Text(
            stringResource(R.string.temperature, weather.temp.roundTo(1)),
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
            modifier = Modifier.weight(0.6f),
        )
        Text(
            "${weather.windSpeed.roundTo(1)}${windSpeedUnit.label}",
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.weight(2f),
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InfoTopBar(
    address: String,
    onBackClick: () -> Unit,
    onSettingsClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    CenterAlignedTopAppBar(
        modifier = modifier,
        navigationIcon = {
            IconButton(onClick = onBackClick) {
                Icon(
                    imageVector = Icons.AutoMirrored.Default.ArrowBack,
                    contentDescription = "Back",
                )
            }
        },
        title = {
            Row {
                Text(
                    address,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 1,
                    textAlign = TextAlign.Center,
                )
            }
        },
        actions = {
            IconButton(onClick = onSettingsClick) {
                Icon(
                    painter = painterResource(R.drawable.ic_setting),
                    contentDescription = "Back",
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
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(25.dp),
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = "$value$unit",
            style = textStyle,
        )
    }
}
