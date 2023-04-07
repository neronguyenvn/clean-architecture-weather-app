package com.example.weatherjourney.features.recommendation.presentation.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.weatherjourney.R
import com.example.weatherjourney.presentation.theme.White70

@Composable
fun AqiNotificationItem(
    firstTimeLine: String,
    secondTimeLine: String,
    info: String,
    adviceRes1: String,
    adviceRes2: String
) {
    Card {
        Column(Modifier.padding(16.dp)) {
            Text(
                stringResource(R.string.aqi_notification),
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                "$firstTimeLine - $secondTimeLine",
                style = MaterialTheme.typography.labelMedium.copy(White70)
            )
            Spacer(Modifier.height(16.dp))
            Text(
                info,
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(Modifier.height(16.dp))
            if (adviceRes2.isBlank()) {
                Text(
                    stringResource(R.string.general_and_sensitive_population),
                    style = MaterialTheme.typography.labelMedium.copy(White70)
                )
                Text(
                    adviceRes1,
                    style = MaterialTheme.typography.bodyMedium
                )
            } else {
                Text(
                    stringResource(R.string.general_population),
                    style = MaterialTheme.typography.labelMedium.copy(White70)
                )
                Text(
                    adviceRes1,
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(Modifier.height(16.dp))
                Text(
                    stringResource(R.string.sensitive_population),
                    style = MaterialTheme.typography.labelMedium.copy(White70)
                )
                Text(
                    adviceRes2,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}
