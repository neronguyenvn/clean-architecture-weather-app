package com.example.weatherjourney.feature.settings

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
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
import com.example.weatherjourney.core.model.PressureUnit
import com.example.weatherjourney.core.model.TemperatureUnit
import com.example.weatherjourney.core.model.TimeFormatUnit
import com.example.weatherjourney.core.model.WeatherUnit
import com.example.weatherjourney.core.model.WindSpeedUnit
import com.example.weatherjourney.presentation.theme.White70

@Composable
fun SettingsRoute(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: SettingViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Column(modifier = modifier) {
        BasicTopBar(
            title = stringResource(R.string.setting),
            onBackClick = onBackClick
        )
        SettingsScreen(
            uiState = uiState,
            onTemperatureUnitChange = { viewModel.onTemperatureChanged(it as TemperatureUnit) },
            onWindSpeedUnitChange = { viewModel.onWindSpeedChanged(it as WindSpeedUnit) },
            onPressureUnitChange = { viewModel.onPressureChanged(it as PressureUnit) },
            onTimeFormatUnitChange = { viewModel.onTimeFormatChanged(it as TimeFormatUnit) },
        )
    }
}

@Composable
fun SettingsScreen(
    uiState: SettingsUiState,
    onTemperatureUnitChange: (WeatherUnit) -> Unit,
    onWindSpeedUnitChange: (WeatherUnit) -> Unit,
    onPressureUnitChange: (WeatherUnit) -> Unit,
    onTimeFormatUnitChange: (WeatherUnit) -> Unit,
    modifier: Modifier = Modifier,
) {
    val screenPadding = PaddingValues(
        vertical = dimensionResource(R.dimen.vertical_margin),
        horizontal = 16.dp,
    )

    Box(contentAlignment = Alignment.Center) {
        when (uiState) {
            SettingsUiState.Loading -> CircularProgressIndicator()
            is SettingsUiState.Success -> Column(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = modifier
                    .fillMaxWidth()
                    .padding(screenPadding),
            ) {
                Text(
                    stringResource(R.string.units),
                    style = MaterialTheme.typography.labelLarge.copy(color = White70),
                )
                UnitRowItem(
                    title = R.string.temperature_unit,
                    options = TemperatureUnit.entries,
                    selected = uiState.data.temperatureUnit,
                    onUnitClick = onTemperatureUnitChange,
                )
                UnitRowItem(
                    title = R.string.wind_speed_unit,
                    options = WindSpeedUnit.entries,
                    selected = uiState.data.windSpeedUnit,
                    onUnitClick = onWindSpeedUnitChange,
                )
                UnitRowItem(
                    title = R.string.pressure_unit,
                    options = PressureUnit.entries,
                    selected = uiState.data.pressureUnit,
                    onUnitClick = onPressureUnitChange,
                )
                UnitRowItem(
                    title = R.string.time_format_unit,
                    options = TimeFormatUnit.entries,
                    selected = uiState.data.timeFormatUnit,
                    onUnitClick = onTimeFormatUnitChange,
                )
            }
        }
    }
}

@Composable
fun UnitRowItem(
    @StringRes title: Int,
    options: List<WeatherUnit>,
    selected: WeatherUnit,
    onUnitClick: (WeatherUnit) -> Unit,
) {
    SingleChoiceSegmentedButtonRow {
        options.forEachIndexed { index, unit ->
            SegmentedButton(
                shape = SegmentedButtonDefaults.itemShape(
                    index = index,
                    count = options.size
                ),
                onClick = { onUnitClick(unit) },
                selected = unit == selected
            ) {
                Text(unit.label)
            }
        }
    }
}
