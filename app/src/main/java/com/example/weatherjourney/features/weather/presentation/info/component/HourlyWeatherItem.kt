package com.example.weatherjourney.features.weather.presentation.info.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.weatherjourney.R
import com.example.weatherjourney.features.weather.domain.model.unit.WindSpeedUnit
import com.example.weatherjourney.features.weather.domain.model.weather.HourlyWeather
import com.example.weatherjourney.util.roundTo

@Suppress("MagicNumber")
@Composable
fun HourlyWeatherItem(
    hourly: HourlyWeather,
    windSpeedUnit: WindSpeedUnit?,
    modifier: Modifier = Modifier,
) {
    windSpeedUnit?.let {
        Row(
            modifier = modifier
                .height(50.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Text(
                hourly.date,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.weight(1f),
            )
            Image(
                painter = painterResource(hourly.weatherType.iconRes),
                contentDescription = null,
                contentScale = ContentScale.FillWidth,
                modifier = Modifier.width(40.dp),
            )
            Text(
                stringResource(R.string.temperature, hourly.temp.roundTo(1)),
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                modifier = Modifier.weight(0.6f),
            )
            Text(
                "${hourly.windSpeed.roundTo(1)}${windSpeedUnit.label}",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.weight(2f),
            )
        }
    }
}
