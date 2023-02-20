package com.example.weatherjourney.weather.presentation.notification

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.weatherjourney.R
import com.example.weatherjourney.presentation.component.BasicTopBar
import com.example.weatherjourney.presentation.component.LoadingContent
import com.example.weatherjourney.presentation.theme.White70
import com.example.weatherjourney.util.UiEvent

@Composable
fun WeatherNotificationScreen(
    snackbarHostState: SnackbarHostState,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: WeatherNotificationViewModel = hiltViewModel()
) {
    Scaffold(
        modifier = modifier,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = { BasicTopBar(stringResource(R.string.notification), onBackClick) }
    ) { paddingValues ->

        val uiState = viewModel.uiState
        val context = LocalContext.current

        WeatherNotificationScreenContent(
            uiState = uiState,
            modifier = Modifier.padding(paddingValues),
            onRefresh = { viewModel.onEvent(WeatherNotificationEvent.OnRefresh) }
        )

        LaunchedEffect(true) {
            viewModel.uiEvent.collect { event ->
                when (event) {
                    is UiEvent.ShowSnackbar -> snackbarHostState.showSnackbar(
                        event.message.asString(
                            context
                        )
                    )

                    else -> Unit
                }
            }
        }
    }
}

@Composable
fun WeatherNotificationScreenContent(
    uiState: WeatherNotificationUiState,
    onRefresh: () -> Unit,
    modifier: Modifier = Modifier
) {
    val screenPadding = PaddingValues(
        vertical = dimensionResource(R.dimen.vertical_margin),
        horizontal = 16.dp
    )

    LoadingContent(isLoading = uiState.isLoading, onRefresh = onRefresh, modifier) {
        LazyColumn(
            Modifier
                .fillMaxWidth()
                .padding(screenPadding)
        ) {
            uiState.weatherAdviceState.uvAdvice?.let {
                item {
                    Card {
                        Column(Modifier.padding(16.dp)) {
                            Text(
                                stringResource(R.string.uv_notification),
                                style = MaterialTheme.typography.titleMedium
                            )
                            Text(
                                "${it.firstTimeLine.asString()} - ${it.secondTimeLine.asString()}",
                                style = MaterialTheme.typography.labelMedium.copy(White70)
                            )
                            Spacer(Modifier.height(16.dp))
                            Text(
                                "${stringResource(it.infoRes)}\n${stringResource(it.adviceRes)}",
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }
            }
        }
    }
}
