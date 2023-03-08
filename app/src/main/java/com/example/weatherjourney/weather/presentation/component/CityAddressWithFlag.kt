package com.example.weatherjourney.weather.presentation.component

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.weatherjourney.util.toFlagEmoji

@Composable
fun CityAddressWithFlag(
    countryCode: String,
    cityAddress: String,
    modifier: Modifier = Modifier
) {
    Row(modifier, verticalAlignment = Alignment.CenterVertically) {
        Text(countryCode.toFlagEmoji())
        Spacer(Modifier.width(8.dp))
        Text(cityAddress, style = MaterialTheme.typography.bodyLarge)
    }
}
