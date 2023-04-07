package com.example.weatherjourney.features.recommendation.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.weatherjourney.R
import com.example.weatherjourney.features.recommendation.presentation.component.AqiNotificationItem
import com.example.weatherjourney.features.recommendation.presentation.component.UvNotificationItem
import com.example.weatherjourney.presentation.component.BasicTopBar
import com.example.weatherjourney.presentation.component.LoadingContent

@Composable
fun RecommendationScreen(
    snackbarHostState: SnackbarHostState,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: RecommendationViewModel = hiltViewModel()
) {
    Scaffold(
        modifier = modifier,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = { BasicTopBar(stringResource(R.string.notification), onBackClick) }
    ) { paddingValues ->

        val uiState by viewModel.uiState.collectAsStateWithLifecycle()

        WeatherNotificationScreenContent(
            uiState = uiState,
            modifier = Modifier.padding(paddingValues),
            onRefresh = viewModel::onRefresh
        )

        // Check for user messages to display on the screen
        uiState.userMessage?.let { userMessage ->
            val snackbarText = userMessage.message?.asString()
            LaunchedEffect(snackbarHostState, viewModel, snackbarText) {
                snackbarText?.let {
                    snackbarHostState.showSnackbar(it)
                    viewModel.onHandleUserMessageDone()
                }
            }
        }
    }
}

@Composable
fun WeatherNotificationScreenContent(
    uiState: RecommendationUiState,
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
                .padding(screenPadding),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            uiState.recommendations?.uvRecommendation?.let {
                item {
                    UvNotificationItem(
                        title = stringResource(R.string.uv_notification),
                        firstTimeLine = it.firstTimeLine,
                        secondTimeLine = it.secondTimeLine,
                        info = stringResource(it.infoRes),
                        adviceRes = stringResource(it.adviceRes)
                    )
                }
            }
            uiState.recommendations?.aqiRecommendation?.let {
                item {
                    AqiNotificationItem(
                        firstTimeLine = it.firstTimeLine,
                        secondTimeLine = it.secondTimeLine,
                        info = stringResource(it.infoRes),
                        adviceRes1 = stringResource(it.adviceRes),
                        adviceRes2 = it.adviceRes2?.let { id -> stringResource(id) } ?: ""
                    )
                }
            }
        }
    }
}
