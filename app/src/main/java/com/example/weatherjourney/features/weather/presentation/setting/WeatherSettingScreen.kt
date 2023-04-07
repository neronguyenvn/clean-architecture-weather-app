package com.example.weatherjourney.features.weather.presentation.setting

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.weatherjourney.R
import com.example.weatherjourney.features.weather.domain.model.unit.AllUnit
import com.example.weatherjourney.features.weather.domain.model.unit.PressureUnit
import com.example.weatherjourney.features.weather.domain.model.unit.TemperatureUnit
import com.example.weatherjourney.features.weather.domain.model.unit.TimeFormatUnit
import com.example.weatherjourney.features.weather.domain.model.unit.WindSpeedUnit
import com.example.weatherjourney.features.weather.presentation.setting.component.UnitItem
import com.example.weatherjourney.presentation.component.BasicTopBar
import com.example.weatherjourney.presentation.theme.White70

@Composable
fun WeatherSettingScreen(
    snackbarHostState: SnackbarHostState,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: WeatherSettingViewModel = hiltViewModel()
) {
    Scaffold(
        modifier = modifier,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = { BasicTopBar(stringResource(R.string.setting), onBackClick) }
    ) { paddingValues ->

        val uiState by viewModel.uiState.collectAsStateWithLifecycle()

        WeatherSettingScreenContent(
            uiState = uiState,
            onTemperatureUnitUpdate = viewModel::onTemperatureUnitUpdate,
            onWindSpeedUnitUpdate = viewModel::onWindSpeedUnitUpdate,
            onPressureUnitUpdate = viewModel::onPressureUnitUpdate,
            onTimeFormatUnitUpdate = viewModel::onTimeFormatUnitUpdate,
            modifier = Modifier.padding(paddingValues)
        )
    }
}

@Composable
fun WeatherSettingScreenContent(
    uiState: AllUnit?,
    onTemperatureUnitUpdate: (String) -> Unit,
    onWindSpeedUnitUpdate: (String) -> Unit,
    onPressureUnitUpdate: (String) -> Unit,
    onTimeFormatUnitUpdate: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val screenPadding = PaddingValues(
        vertical = dimensionResource(R.dimen.vertical_margin),
        horizontal = 16.dp
    )

    val temperatureUnits =
        TemperatureUnit.values().filter { it != TemperatureUnit.NULL }.map { it.label }
    val windSpeedUnits = WindSpeedUnit.values().filter { it != WindSpeedUnit.NULL }.map { it.label }
    val pressureUnits = PressureUnit.values().filter { it != PressureUnit.NULL }.map { it.label }
    val timeFormatUnits =
        TimeFormatUnit.values().filter { it != TimeFormatUnit.NULL }.map { it.label }

    uiState?.let {
        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = modifier
                .fillMaxWidth()
                .padding(screenPadding)
        ) {
            Text(
                stringResource(R.string.units),
                style = MaterialTheme.typography.labelLarge.copy(color = White70)
            )
            UnitItem(
                title = R.string.temperature_unit,
                segments = temperatureUnits,
                selectedSegment = it.temperature.label,
                onSegmentSelected = onTemperatureUnitUpdate
            )
            UnitItem(
                title = R.string.wind_speed_unit,
                segments = windSpeedUnits,
                selectedSegment = it.windSpeed.label,
                onSegmentSelected = onWindSpeedUnitUpdate
            )
            UnitItem(
                title = R.string.pressure_unit,
                segments = pressureUnits,
                selectedSegment = it.pressure.label,
                onSegmentSelected = onPressureUnitUpdate
            )
            UnitItem(
                title = R.string.time_format_unit,
                segments = timeFormatUnits,
                selectedSegment = it.timeFormat.label,
                onSegmentSelected = onTimeFormatUnitUpdate
            )
        }
    }
}
