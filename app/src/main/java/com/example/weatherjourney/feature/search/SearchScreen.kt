package com.example.weatherjourney.feature.search

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.weatherjourney.R
import com.example.weatherjourney.core.designsystem.component.AddressWithFlag
import com.example.weatherjourney.core.model.Location
import com.example.weatherjourney.feature.home.WeatherSearchBar

@Composable
fun SearchRoute(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: SearchViewModel = hiltViewModel()
) {
    val query by viewModel.query.collectAsState()
    val searchResults by viewModel.searchResults.collectAsStateWithLifecycle()

    SearchScreen(
        query = query,
        searchResults = searchResults,
        onBackClick = onBackClick,
        onLocationClick = {},
        onQueryChange = viewModel::onQueryChanged,
        modifier = modifier
    )
}

@Composable
fun SearchScreen(
    query: String,
    searchResults: List<Location>,
    onQueryChange: (String) -> Unit,
    onBackClick: () -> Unit,
    onLocationClick: (Location) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier) {
        WeatherSearchBar(
            query = query,
            onQueryChange = onQueryChange,
            onBackClick = onBackClick
        )
        LazyColumn {
            if (searchResults.isEmpty()) {
                item { NoResultText() }
            }
            searchResults(
                locations = searchResults,
                onLocationClick = onLocationClick
            )
        }
    }
}

@Composable
fun NoResultText(modifier: Modifier = Modifier) {
    Text(
        stringResource(R.string.no_result),
        style = MaterialTheme.typography.titleMedium,
        textAlign = TextAlign.Center,
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 8.dp),
    )
}

fun LazyListScope.searchResults(
    locations: List<Location>,
    onLocationClick: (Location) -> Unit,
    itemModifier: Modifier = Modifier,
) {
    items(
        items = locations,
        key = { it.id }
    ) { location ->
        Column(
            modifier = itemModifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .clickable { onLocationClick(location) },
        ) {
            Spacer(Modifier.height(16.dp))
            AddressWithFlag(countryCode = location.countryCode, address = location.address)
            Spacer(Modifier.height(16.dp))
            HorizontalDivider()
        }
    }
}

