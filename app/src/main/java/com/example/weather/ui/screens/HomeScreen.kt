package com.example.weather.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.MaterialTheme.typography
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Search
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.ExperimentalLifecycleComposeApi
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.weather.R
import com.example.weather.ui.theme.White
import java.util.*

@OptIn(ExperimentalLifecycleComposeApi::class)
@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    weatherViewModel: WeatherViewModel = viewModel(),
) {
    Column(
        modifier = modifier.padding(horizontal = 24.dp, vertical = 40.dp),
    ) {
        val uiState by weatherViewModel.uiState.collectAsStateWithLifecycle()

        SearchField(
            value = uiState.city,
            onValueChange = weatherViewModel::updateCity,
            onActionDone = { weatherViewModel.getWeather(uiState.city) }
        )
        Spacer(modifier = Modifier.height(48.dp))
        CurrentWeatherContent(uiState.date, uiState.temp, uiState.weather)
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
        unfocusedBorderColor = Color.Transparent
    )
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        singleLine = true,
        keyboardOptions = KeyboardOptions(
            imeAction = ImeAction.Done,
            capitalization = KeyboardCapitalization.Words,
        ),
        keyboardActions = KeyboardActions(
            onDone = { onActionDone() }
        ),
        colors = textFieldColors,
        modifier = modifier.fillMaxWidth(),
        leadingIcon = { Icon(Icons.Outlined.Search, contentDescription = null) },
    )
}

@Composable
fun CurrentWeatherContent(
    date: String,
    temperature: Int,
    weather: String,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.padding(start = 24.dp)) {
        Text(text = date, style = typography.body1.copy(color = White))
        Text(
            text = stringResource(id = R.string.temperature, temperature),
            style = typography.h2.copy(color = White),
        )
        Text(text = weather, style = typography.h5.copy(color = White))
    }
}