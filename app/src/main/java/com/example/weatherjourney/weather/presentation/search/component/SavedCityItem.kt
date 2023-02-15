package com.example.weatherjourney.weather.presentation.search.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.weatherjourney.R
import com.example.weatherjourney.weather.domain.model.SavedCity

@Composable
fun SavedCityItem(
    city: SavedCity,
    modifier: Modifier = Modifier,
    onCityClick: (SavedCity) -> Unit
) {
    Row(
        modifier = modifier
            .clickable { onCityClick(city) }
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (city.isCurrentLocation) {
            Icon(Icons.Outlined.LocationOn, null)
            Spacer(Modifier.width(4.dp))
        }
        Text(
            text = stringResource(R.string.daily_weather, city.cityAddress, city.weatherType.weatherDesc),
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.weight(10f)
        )
        Spacer(Modifier.weight(1f))
        Text(
            stringResource(R.string.temperature, city.temp),
            style = MaterialTheme.typography.bodyMedium
        )
        Spacer(Modifier.width(4.dp))
        Image(
            painter = painterResource(city.weatherType.iconRes),
            contentDescription = null,
            modifier = Modifier.width(40.dp)
        )
    }
}
