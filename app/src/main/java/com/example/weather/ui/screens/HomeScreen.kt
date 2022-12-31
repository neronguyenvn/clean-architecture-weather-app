package com.example.weather.ui.screens

import android.Manifest
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.MaterialTheme.typography
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.ExperimentalLifecycleComposeApi
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import coil.imageLoader
import coil.request.ImageRequest
import coil.util.DebugLogger
import com.example.weather.R
import com.example.weather.model.weather.DailyWeather
import com.example.weather.ui.theme.Poppins
import com.example.weather.utils.PermissionAction

/**
 * Ui component for Weather Home screen
 */
@OptIn(ExperimentalLifecycleComposeApi::class, ExperimentalMaterialApi::class)
@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    weatherViewModel: WeatherViewModel = viewModel()
) {
    val uiState by weatherViewModel.uiState.collectAsStateWithLifecycle()
    val isRefreshing by weatherViewModel.isRefreshing.collectAsStateWithLifecycle()
    val context = LocalContext.current

    val pullRefreshState =
        rememberPullRefreshState(
            refreshing = isRefreshing,
            onRefresh = {
                try {
                    weatherViewModel.getAllWeather(uiState.cityName)
                } catch (ex: Exception) {
                    Toast.makeText(context, ex.toString(), Toast.LENGTH_LONG).show()
                }
            }
        )

    Box(
        modifier = modifier
            .pullRefresh(pullRefreshState)
            .fillMaxSize()
    ) {
        Image(
            modifier = Modifier.fillMaxSize(),
            painter = painterResource(id = uiState.bgImg),
            contentDescription = null,
            contentScale = ContentScale.FillHeight
        )

        if (uiState.shouldDoLocationAction) {
            PermissionScreen(
                permission = Manifest.permission.ACCESS_FINE_LOCATION
            ) { permissionAction ->
                when (permissionAction) {
                    is PermissionAction.OnPermissionGranted -> {
                        weatherViewModel.updateShouldDoLocationAction(false)
                        try {
                            weatherViewModel.getCurrentCoordinateAllWeather()
                        } catch (ex: Exception) {
                            Toast.makeText(context, ex.toString(), Toast.LENGTH_LONG).show()
                        }
                    }
                    is PermissionAction.OnPermissionDenied -> {
                        weatherViewModel.updateShouldDoLocationAction(false)
                    }
                }
            }
        }

        Column(Modifier.padding(horizontal = 24.dp, vertical = 40.dp)) {
            SearchField(
                value = uiState.cityName,
                onValueChange = weatherViewModel::updateCityName,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                keyboardActions = KeyboardActions(
                    onSearch = {
                        try {
                            weatherViewModel.getAllWeather(uiState.cityName)
                        } catch (ex: Exception) {
                            Toast.makeText(context, ex.toString(), Toast.LENGTH_LONG).show()
                        }
                    }
                )
            )
            Spacer(modifier = Modifier.height(48.dp))
            CurrentWeatherContent(uiState)
            Spacer(modifier = Modifier.height(48.dp))
            DailyWeatherContent(listDaily = uiState.listDaily)
        }

        PullRefreshIndicator(
            isRefreshing,
            pullRefreshState,
            Modifier.align(Alignment.TopCenter)
        )
    }
}

/**
 * Ui component for input CityName then tap Done button to search for All Weather of that city
 */
@Composable
fun SearchField(
    value: String,
    onValueChange: (String) -> Unit,
    keyboardOptions: KeyboardOptions,
    keyboardActions: KeyboardActions,
    modifier: Modifier = Modifier
) {
    val textFieldColors = TextFieldDefaults.outlinedTextFieldColors(
        backgroundColor = MaterialTheme.colors.background,
        focusedBorderColor = Color.Transparent,
        unfocusedBorderColor = Color.Transparent
    )
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        singleLine = true,
        keyboardOptions = keyboardOptions,
        keyboardActions = keyboardActions,
        colors = textFieldColors,
        modifier = modifier.fillMaxWidth(),
        leadingIcon = { Icon(Icons.Outlined.Search, contentDescription = null) },
        textStyle = TextStyle(
            fontFamily = Poppins,
            fontSize = 16.sp
        )
    )
}

/**
 * Ui component for Current Weather info
 */
@Composable
fun CurrentWeatherContent(
    weatherUiState: WeatherUiState,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.padding(start = 24.dp)) {
        Text(text = weatherUiState.date, style = typography.body2)
        if (weatherUiState.temp != "") {
            Text(
                text = stringResource(id = R.string.temperature, weatherUiState.temp),
                style = typography.h2
            )
        }
        Text(text = weatherUiState.weather, style = typography.h5)
    }
}

/**
 * Ui component for all Daily Weather info
 */
@Composable
fun DailyWeatherContent(listDaily: List<DailyWeather>) {
    LazyColumn {
        items(listDaily) { daily ->
            DailyWeatherItem(daily)
        }
    }
}

/**
 * Ui component for one Daily Weather item
 */
@Composable
fun DailyWeatherItem(
    daily: DailyWeather,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        val imageLoader =
            LocalContext.current.imageLoader.newBuilder().logger(DebugLogger()).build()

        AsyncImage(
            model = ImageRequest.Builder(context = LocalContext.current).data(daily.iconUrl)
                .crossfade(true).build(),
            placeholder = painterResource(id = R.drawable.loading_img),
            contentDescription = null,
            imageLoader = imageLoader
        )
        Text(
            text = stringResource(id = R.string.daily_weather, daily.date, daily.weather),
            style = typography.body1
        )
        Spacer(modifier = Modifier.weight(1f))
        Text(
            text = stringResource(id = R.string.max_min_temperature, daily.maxTemp, daily.minTemp),
            style = typography.body1
        )
    }
}
