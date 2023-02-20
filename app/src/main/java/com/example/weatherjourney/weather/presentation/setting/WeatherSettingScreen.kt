package com.example.weatherjourney.weather.presentation.setting

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.weatherjourney.R
import com.example.weatherjourney.presentation.theme.White70
import com.example.weatherjourney.presentation.component.BasicTopBar
import com.example.weatherjourney.weather.presentation.setting.component.UnitItem

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

        val uiState = viewModel.uiState

        WeatherSettingScreenContent(
            uiState = uiState,
            onTemperatureLabelUpdate = {
                viewModel.onEvent(WeatherSettingEvent.OnTemperatureLabelUpdate(it))
            },
            onWindSpeedLabelUpdate = {
                viewModel.onEvent(WeatherSettingEvent.OnWindSpeedLabelUpdate(it))
            },
            modifier = Modifier.padding(paddingValues)
        )
    }
}

@Composable
fun WeatherSettingScreenContent(
    uiState: WeatherSettingUiState,
    onTemperatureLabelUpdate: (String) -> Unit,
    onWindSpeedLabelUpdate: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val screenPadding = PaddingValues(
        vertical = dimensionResource(R.dimen.vertical_margin),
        horizontal = 16.dp
    )

    val temperatureUnits = stringArrayResource(R.array.temperature_units).toList()
    val windSpeedUnits = stringArrayResource(R.array.wind_speed_units).toList()

    Column(
        modifier
            .fillMaxWidth()
            .padding(screenPadding)
    ) {
        Text(
            stringResource(R.string.units),
            style = MaterialTheme.typography.labelLarge.copy(color = White70)
        )
        Spacer(Modifier.height(12.dp))
        UnitItem(
            title = R.string.temperature_unit,
            segments = temperatureUnits,
            selectedSegment = uiState.temperatureLabel,
            onSegmentSelected = onTemperatureLabelUpdate
        )
        Spacer(Modifier.height(12.dp))
        UnitItem(
            title = R.string.wind_speed_unit,
            segments = windSpeedUnits,
            selectedSegment = uiState.windSpeedLabel,
            onSegmentSelected = onWindSpeedLabelUpdate
        )
    }
}
