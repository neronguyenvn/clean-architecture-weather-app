package com.example.weatherjourney.weather.presentation.info.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.weatherjourney.R
import com.example.weatherjourney.weather.domain.model.weather.DailyWeather
import kotlin.math.roundToInt

@Composable
fun DailyWeatherItem(
    daily: DailyWeather,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween,
        modifier = modifier
            .border(1.dp, MaterialTheme.colorScheme.onBackground, RoundedCornerShape(20.dp))
            .padding(16.dp)
            .height(135.dp)
            .width(90.dp)
    ) {
        val context = LocalContext.current

        Text(
            text = "${daily.date.asString(context)}\n${daily.weatherType.weatherDesc}",
            style = MaterialTheme.typography.titleSmall,
            textAlign = TextAlign.Center
        )
        Image(
            painter = painterResource(daily.weatherType.iconRes),
            contentDescription = null,
            modifier = Modifier.width(40.dp)
        )
        Text(
            text = stringResource(
                R.string.max_min_temperature,
                daily.maxTemp.roundToInt(),
                daily.minTemp.roundToInt()
            ),
            style = MaterialTheme.typography.bodyMedium
        )
    }
}
