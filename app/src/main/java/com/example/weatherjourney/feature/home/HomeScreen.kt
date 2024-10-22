package com.example.weatherjourney.feature.home

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.weatherjourney.R
import com.example.weatherjourney.core.common.util.roundTo
import com.example.weatherjourney.core.designsystem.component.AddressWithFlag
import com.example.weatherjourney.core.designsystem.component.SearchTopBar
import com.example.weatherjourney.core.designsystem.component.SearchTopBarAction
import com.example.weatherjourney.core.model.LocationWithWeather
import com.example.weatherjourney.feature.home.HomeUiState.*

private const val TAG = "HomeScreen"

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
    Log.d(TAG, "Current UiState: $uiState")

    Column {
        SearchTopBar(action = SearchTopBarAction.NoBack(onSearchClick))
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
        LazyColumn(modifier = Modifier.fillMaxWidth()) {
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
