package com.example.weatherjourney.weather.presentation.search.component

import androidx.compose.foundation.clickable
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
import androidx.compose.ui.unit.dp
import com.example.weatherjourney.presentation.component.HorizontalDivider
import com.example.weatherjourney.weather.domain.model.SuggestionCity

@Composable
fun SuggestionCityItem(
    city: SuggestionCity,
    modifier: Modifier = Modifier,
    onCityClick: (SuggestionCity) -> Unit
) {
    Column(
        modifier = modifier
            .clickable { onCityClick(city) }
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        Spacer(Modifier.height(16.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(city.countryFlag)
            Spacer(Modifier.width(8.dp))
            Text(city.cityAddress, style = MaterialTheme.typography.bodyLarge)
        }
        Spacer(Modifier.height(16.dp))
        HorizontalDivider()
    }
}
