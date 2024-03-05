package com.example.weatherjourney.core.designsystem.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.weatherjourney.R

@Composable
fun CurrentLocationField(
    onFieldClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onFieldClick() },
    ) {
        Row(Modifier.padding(16.dp)) {
            Icon(painterResource(R.drawable.ic_my_location), null)
            Spacer(Modifier.width(8.dp))
            Text(
                stringResource(R.string.get_your_location_weather),
                style = MaterialTheme.typography.bodyLarge,
            )
        }
        HorizontalDivider(Modifier.padding(horizontal = 16.dp))
    }
}