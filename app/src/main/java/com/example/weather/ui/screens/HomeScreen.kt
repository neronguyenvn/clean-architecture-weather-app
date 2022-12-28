package com.example.weather.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.MaterialTheme.typography
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Search
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
import androidx.compose.ui.text.input.KeyboardCapitalization
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
import java.util.*

@OptIn(ExperimentalLifecycleComposeApi::class)
@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    weatherViewModel: WeatherViewModel = viewModel(),
) {
    Box {
        val uiState by weatherViewModel.uiState.collectAsStateWithLifecycle()

        Image(
            modifier = Modifier.fillMaxSize(),
            painter = painterResource(uiState.bgImg),
            contentDescription = null,
            contentScale = ContentScale.FillHeight
        )
        Column(modifier = modifier.padding(horizontal = 24.dp, vertical = 40.dp)) {

            SearchField(
                value = uiState.city,
                onValueChange = weatherViewModel::updateCity,
                onActionDone = { weatherViewModel.getWeather(uiState.city) }
            )
            Spacer(modifier = Modifier.height(48.dp))
            CurrentWeatherContent(uiState.date, uiState.temp, uiState.weather)
            Spacer(modifier = Modifier.height(48.dp))
            DailyWeatherContent(listDaily = uiState.listDaily)
        }
    }
}

@Composable
fun SearchField(
    modifier: Modifier = Modifier,
    value: String = "",
    onValueChange: (String) -> Unit = {},
    onActionDone: () -> Unit = {}
) {
    val textFieldColors = TextFieldDefaults.outlinedTextFieldColors(
        backgroundColor = MaterialTheme.colors.background,
        focusedBorderColor = Color.Transparent,
        unfocusedBorderColor = Color.Transparent,
    )
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        singleLine = true,
        keyboardOptions = KeyboardOptions(
            imeAction = ImeAction.Done,
            capitalization = KeyboardCapitalization.Words,
        ),
        keyboardActions = KeyboardActions(onDone = { onActionDone() }),
        colors = textFieldColors,
        modifier = modifier.fillMaxWidth(),
        leadingIcon = { Icon(Icons.Outlined.Search, contentDescription = null) },
        textStyle = TextStyle(
            fontFamily = Poppins,
            fontSize = 16.sp,
        )
    )
}

@Composable
fun CurrentWeatherContent(
    date: String, temperature: Int, weather: String, modifier: Modifier = Modifier
) {
    Column(modifier = modifier.padding(start = 24.dp)) {
        Text(text = date, style = typography.body2)
        Text(
            text = stringResource(id = R.string.temperature, temperature),
            style = typography.h2,
        )
        Text(text = weather, style = typography.h5)
    }
}

@Composable
fun DailyWeatherContent(listDaily: List<DailyWeather>) {
    LazyColumn {
        items(listDaily) { daily ->
            DailyWeatherItem(daily)
        }
    }
}

@Composable
fun DailyWeatherItem(
    daily: DailyWeather, modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically
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