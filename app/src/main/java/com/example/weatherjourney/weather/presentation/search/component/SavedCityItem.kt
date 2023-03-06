package com.example.weatherjourney.weather.presentation.search.component

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.weatherjourney.R
import com.example.weatherjourney.presentation.component.HorizontalDivider
import com.example.weatherjourney.presentation.theme.White70
import com.example.weatherjourney.weather.domain.model.SavedCity

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun SavedCityItem(
    city: SavedCity,
    modifier: Modifier = Modifier,
    onCityClick: (SavedCity) -> Unit,
    onCityLongClick: (SavedCity) -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .combinedClickable(
                onClick = { onCityClick(city) },
                onLongClick = { onCityLongClick(city) }
            )
    ) {
        Spacer(Modifier.height(16.dp))
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (city.isCurrentLocation) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = city.cityAddress,
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Text(
                        text = stringResource(R.string.your_location),
                        style = MaterialTheme.typography.labelMedium.copy(White70)
                    )
                }
            } else {
                Text(
                    text = city.cityAddress,
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.weight(1f)
                )
            }
            Spacer(Modifier.width(8.dp))
            Text(
                stringResource(R.string.temperature, city.temp),
                style = MaterialTheme.typography.bodyLarge
            )
            Spacer(Modifier.width(8.dp))
            Image(
                painter = painterResource(city.weatherType.iconRes),
                contentDescription = null,
                modifier = Modifier.width(30.dp)
            )
        }
        Spacer(Modifier.height(16.dp))
        HorizontalDivider()
    }
}
