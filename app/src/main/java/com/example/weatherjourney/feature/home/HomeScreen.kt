package com.example.weatherjourney.feature.home

import android.annotation.SuppressLint
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.weatherjourney.R
import com.example.weatherjourney.core.common.util.roundTo
import com.example.weatherjourney.core.designsystem.component.AddressWithFlag
import com.example.weatherjourney.core.designsystem.component.HorizontalDivider
import com.example.weatherjourney.core.model.LocationWithWeather
import com.example.weatherjourney.feature.home.HomeUiState.*
import com.example.weatherjourney.presentation.theme.White70

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun HomeRoute(
    onSearchClick: () -> Unit,
    onLocationClick: (Int) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val locationsWithWeather by viewModel.locationsWithWeather.collectAsStateWithLifecycle()

    Column {
        WeatherSearchBar(onClick = onSearchClick)
        HomeScreen(
            uiState = uiState,
            locationsWithWeather = locationsWithWeather,
            onLocationClick = onLocationClick,
            modifier = modifier
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    uiState: HomeUiState,
    locationsWithWeather: List<LocationWithWeather>,
    onLocationClick: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    PullToRefreshBox(
        isRefreshing = uiState is Loading,
        onRefresh = { }, // TODO
        modifier = modifier,
    ) {
        LazyColumn {
            items(
                items = locationsWithWeather,
                key = { it.location.id }
            ) {
                LocationWithWeatherItem(
                    locationWithWeather = it,
                    onClick = onLocationClick,
                )
            }
        }
    }
}


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun LocationWithWeatherItem(
    locationWithWeather: LocationWithWeather,
    onClick: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .clickable { onClick(locationWithWeather.id) },
    ) {
        Spacer(Modifier.height(16.dp))
        Row(
            verticalAlignment = Alignment.CenterVertically,
        ) {
            AddressWithFlag(
                countryCode = locationWithWeather.location.countryCode,
                address = locationWithWeather.location.address,
                modifier = Modifier.weight(1f),
            )
            Spacer(Modifier.width(8.dp))
            locationWithWeather.weather?.current?.let {
                Text(
                    text = stringResource(R.string.temperature, it.temp.roundTo(1)),
                    style = MaterialTheme.typography.bodyLarge,
                )
                Spacer(Modifier.width(8.dp))
                Image(
                    painter = painterResource(it.weatherType.iconRes),
                    contentDescription = null,
                    modifier = Modifier.width(30.dp),
                )
            }
        }
        Spacer(Modifier.height(16.dp))
        HorizontalDivider()
    }
}

@Composable
fun WeatherSearchBar(
    modifier: Modifier = Modifier,
    query: String = "",
    onBackClick: () -> Unit = {},
    onQueryChange: (String) -> Unit = {},
    onClick: () -> Unit = {}
) {
    val focusRequester = remember { FocusRequester() }
    val keyboardController = LocalSoftwareKeyboardController.current

    Column(
        modifier = modifier
            .padding(8.dp)
            .clickable(onClick = onClick)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp),
        ) {
            IconButton(onClick = onBackClick) {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = stringResource(R.string.back),
                )
            }
            Box(Modifier.weight(1f)) {
                if (query.isBlank()) {
                    Text(
                        color = White70,
                        text = stringResource(R.string.enter_location),
                    )
                }
                BasicTextField(
                    value = query,
                    onValueChange = onQueryChange,
                    textStyle = MaterialTheme.typography.bodyLarge.copy(color = Color.White),
                    singleLine = true,
                    keyboardActions = KeyboardActions(onDone = { keyboardController?.hide() }),
                    cursorBrush = SolidColor(Color.White),
                    modifier = Modifier.focusRequester(focusRequester),
                )
            }
            if (query.isNotBlank()) {
                IconButton(onClick = { onQueryChange("") }) {
                    Icon(Icons.Filled.Close, "Delete input")
                }
            }
        }
        HorizontalDivider()
    }

    LaunchedEffect(true) {
        focusRequester.requestFocus()
    }
}
