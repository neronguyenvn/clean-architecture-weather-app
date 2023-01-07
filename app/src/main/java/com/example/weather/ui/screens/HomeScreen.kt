package com.example.weather.ui.screens

import android.Manifest
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
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme.colors
import androidx.compose.material.MaterialTheme.typography
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Scaffold
import androidx.compose.material.ScaffoldState
import androidx.compose.material.SnackbarDuration
import androidx.compose.material.Text
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.ExperimentalLifecycleComposeApi
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import coil.imageLoader
import coil.request.ImageRequest
import coil.util.DebugLogger
import com.example.weather.R
import com.example.weather.model.utils.PermissionAction
import com.example.weather.model.weather.CurrentWeather
import com.example.weather.model.weather.DailyWeather
import com.example.weather.utils.LoadingContent
import com.example.weather.utils.PermissionContent

/**
 * Ui component for Weather Home screen.
 */
@OptIn(ExperimentalLifecycleComposeApi::class)
@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    viewModel: WeatherViewModel = viewModel(),
    scaffoldState: ScaffoldState = rememberScaffoldState()
) {
    Scaffold(
        scaffoldState = scaffoldState,
        modifier = modifier.fillMaxSize(),
        floatingActionButton = {
            FloatingActionButton(onClick = { viewModel.fetchLocationAllWeather() }) {
                Icon(Icons.Filled.LocationOn, stringResource(R.string.get_current_location_weather))
            }
        }
    ) { paddingValues ->
        val uiState by viewModel.uiState.collectAsStateWithLifecycle()

        Box(Modifier.padding(paddingValues)) {
            Image(
                painter = painterResource(
                    uiState.weatherState.current?.bgImg ?: R.drawable.day_rain
                ),
                contentDescription = null,
                contentScale = ContentScale.FillHeight,
                modifier = Modifier.fillMaxSize()
            )

            HomeScreenContent(
                city = uiState.weatherState.city,
                onCityChanged = { viewModel.updateCity(it) },
                onWeatherSearched = { viewModel.getAllWeather(uiState.weatherState.city) },
                onCityCleared = { viewModel.updateCity("") },
                isLoading = uiState.isLoading,
                onRefresh = { viewModel.getAllWeather(uiState.weatherState.city) },
                current = uiState.weatherState.current,
                listDaily = uiState.weatherState.listDaily
            )
        }

        LaunchedEffect(true) {
            if (!viewModel.tryFetchLastResultWeather()) {
                viewModel.updateShouldFetchCurrentLocationWeather(true)
            }
        }

        if (uiState.weatherState.shouldFetchCurrentLocationWeather) {
            PermissionContent(permission = Manifest.permission.ACCESS_FINE_LOCATION) { permissionAction ->
                if (permissionAction is PermissionAction.OnPermissionGranted) {
                    viewModel.updateShouldFetchCurrentLocationWeather(false)
                    viewModel.fetchLocationAllWeather()
                }
            }
        }

        uiState.userMessage?.let { message ->
            val snackbarText = stringResource(message)
            LaunchedEffect(scaffoldState, viewModel, message, snackbarText) {
                scaffoldState.snackbarHostState.showSnackbar(
                    snackbarText,
                    duration = SnackbarDuration.Short
                )
                viewModel.snackbarMessageShown()
            }
        }
    }
}

@Composable
fun HomeScreenContent(
    city: String,
    onCityChanged: (String) -> Unit,
    onWeatherSearched: () -> Unit,
    onCityCleared: () -> Unit,
    isLoading: Boolean,
    onRefresh: () -> Unit,
    current: CurrentWeather?,
    listDaily: List<DailyWeather>,
    modifier: Modifier = Modifier
) {
    val screenPadding = Modifier.padding(
        horizontal = dimensionResource(R.dimen.horizontal_margin),
        vertical = dimensionResource(R.dimen.vertical_margin)
    )
    val commonModifier = modifier
        .fillMaxWidth()
        .then(screenPadding)

    LoadingContent(isLoading = isLoading, onRefresh = onRefresh) {
        Column(commonModifier) {
            SearchField(city, onCityChanged, onWeatherSearched, onCityCleared)
            Spacer(Modifier.height(24.dp))
            CurrentWeatherContent(current)
            Spacer(Modifier.height(72.dp))
            DailyWeatherContent(listDaily)
        }
    }
}

/**
 * Ui component for input CityName then tap Done button to search for All Weather of that city.
 */
@Composable
fun SearchField(
    city: String,
    onCityChanged: (String) -> Unit,
    onWeatherSearched: () -> Unit,
    onCityCleared: () -> Unit,
    modifier: Modifier = Modifier
) {
    val textFieldColors = TextFieldDefaults.outlinedTextFieldColors(
        backgroundColor = colors.background,
        focusedBorderColor = Color.Transparent
    )

    @Composable
    fun clearIcon() = IconButton(onClick = onCityCleared) {
        Icon(Icons.Outlined.Close, stringResource(R.string.clear_city))
    }

    val focusManager = LocalFocusManager.current

    OutlinedTextField(
        value = city,
        onValueChange = onCityChanged,
        keyboardOptions = KeyboardOptions(
            capitalization = KeyboardCapitalization.Words,
            imeAction = ImeAction.Search
        ),
        keyboardActions = KeyboardActions(
            onSearch = {
                onWeatherSearched()
                focusManager.clearFocus()
            }
        ),
        trailingIcon = {
            if (city.isNotEmpty()) {
                clearIcon()
            }
        },
        colors = textFieldColors,
        singleLine = true,
        leadingIcon = { Icon(Icons.Outlined.Search, contentDescription = null) },
        textStyle = typography.body1.copy(color = colors.onBackground),
        modifier = modifier.fillMaxWidth()
    )
}

/**
 * Ui component for Current Weather info.
 */
@Composable
fun CurrentWeatherContent(
    current: CurrentWeather?,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.padding(start = 36.dp)) {
        current?.let {
            Text(text = current.date, style = typography.body2)
            Text(
                text = stringResource(id = R.string.temperature, current.temp),
                style = typography.h2
            )
            Text(text = current.weather, style = typography.h5)
        }
    }
}

/**
 * Ui component for all Daily Weather info.
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
 * Ui component for one Daily Weather item.
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
            text = stringResource(
                id = R.string.max_min_temperature,
                daily.maxTemp,
                daily.minTemp
            ),
            style = typography.body1
        )
    }
}
