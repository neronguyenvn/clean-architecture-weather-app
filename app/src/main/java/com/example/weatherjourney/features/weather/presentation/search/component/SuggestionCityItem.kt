package com.example.weatherjourney.features.weather.presentation.search.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.weatherjourney.features.weather.domain.model.SuggestionCity
import com.example.weatherjourney.features.weather.presentation.component.CityAddressWithFlag
import com.example.weatherjourney.presentation.component.HorizontalDivider

@Composable
fun SuggestionCityItem(
    city: SuggestionCity,
    modifier: Modifier = Modifier,
    onCityClick: (SuggestionCity) -> Unit,
) {
    Column(
        modifier = modifier
            .clickable { onCityClick(city) }
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
    ) {
        Spacer(Modifier.height(16.dp))
        CityAddressWithFlag(countryCode = city.countryCode, cityAddress = city.cityAddress)
        Spacer(Modifier.height(16.dp))
        HorizontalDivider()
    }
}
