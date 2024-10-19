package com.example.weatherjourney.feature.settings

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.weatherjourney.R
import com.example.weatherjourney.core.designsystem.component.BasicTopBar
import com.example.weatherjourney.core.designsystem.component.SegmentText
import com.example.weatherjourney.core.designsystem.component.SegmentedControl
import com.example.weatherjourney.core.model.unit.AllUnit
import com.example.weatherjourney.presentation.theme.White70

@Composable
fun SettingsScreen(
    modifier: Modifier = Modifier,
    viewModel: SettingViewModel = hiltViewModel(),
) {
    Scaffold(
        modifier = modifier,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = { BasicTopBar(stringResource(R.string.setting), onBackClick) },
    ) { paddingValues ->

        val uiState by viewModel.uiState.collectAsStateWithLifecycle()

        WeatherSettingScreenContent(
            uiState = uiState,
            onTemperatureUnitUpdate = viewModel::onTemperatureChanged,
            onWindSpeedUnitUpdate = viewModel::onWindSpeedChanged,
            onPressureUnitUpdate = viewModel::onPressureChanged,
            onTimeFormatUnitUpdate = viewModel::onTimeFormatChanged,
            modifier = Modifier.padding(paddingValues),
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
    modifier: Modifier = Modifier,
) {
    val screenPadding = PaddingValues(
        vertical = dimensionResource(R.dimen.vertical_margin),
        horizontal = 16.dp,
    )

    val temperatureUnits =
        TemperatureUnit.entries.filter { it != TemperatureUnit.NULL }.map { it.label }
    val windSpeedUnits = WindSpeedUnit.entries.filter { it != WindSpeedUnit.NULL }.map { it.label }
    val pressureUnits = PressureUnit.entries.filter { it != PressureUnit.NULL }.map { it.label }
    val timeFormatUnits =
        TimeFormatUnit.entries.filter { it != TimeFormatUnit.NULL }.map { it.label }

    uiState?.let {
        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = modifier
                .fillMaxWidth()
                .padding(screenPadding),
        ) {
            Text(
                stringResource(R.string.units),
                style = MaterialTheme.typography.labelLarge.copy(color = White70),
            )
            UnitItem(
                title = R.string.temperature_unit,
                segments = temperatureUnits,
                selectedSegment = it.temperature.label,
                onSegmentSelected = onTemperatureUnitUpdate,
            )
            UnitItem(
                title = R.string.wind_speed_unit,
                segments = windSpeedUnits,
                selectedSegment = it.windSpeed.label,
                onSegmentSelected = onWindSpeedUnitUpdate,
            )
            UnitItem(
                title = R.string.pressure_unit,
                segments = pressureUnits,
                selectedSegment = it.pressure.label,
                onSegmentSelected = onPressureUnitUpdate,
            )
            UnitItem(
                title = R.string.time_format_unit,
                segments = timeFormatUnits,
                selectedSegment = it.timeFormat.label,
                onSegmentSelected = onTimeFormatUnitUpdate,
            )
        }
    }
}

@Composable
fun UnitItem(
    @StringRes title: Int,
    segments: List<String>,
    selectedSegment: String,
    onSegmentSelected: (String) -> Unit,
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(
            stringResource(title),
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.weight(1f),
        )
        SegmentedControl(
            segments,
            selectedSegment,
            onSegmentSelected = { onSegmentSelected(it) },
            modifier = Modifier.weight(1f),
        ) {
            SegmentText(it)
        }
    }
}

