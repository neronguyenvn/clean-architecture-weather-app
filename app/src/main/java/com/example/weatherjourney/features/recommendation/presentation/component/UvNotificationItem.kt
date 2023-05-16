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
import androidx.compose.ui.unit.dp
import com.example.weatherjourney.presentation.theme.White70

@Composable
fun UvNotificationItem(
    title: String,
    firstTimeLine: String,
    secondTimeLine: String,
    info: String,
    adviceRes: String,
) {
    Card {
        Column(Modifier.padding(16.dp)) {
            Text(
                title,
                style = MaterialTheme.typography.titleMedium,
            )
            Text(
                "$firstTimeLine - $secondTimeLine",
                style = MaterialTheme.typography.labelMedium.copy(White70),
            )
            Spacer(Modifier.height(16.dp))
            Text(
                info,
                style = MaterialTheme.typography.bodyMedium,
            )
            Spacer(Modifier.height(16.dp))
            Text(
                adviceRes,
                style = MaterialTheme.typography.bodyMedium,
            )
        }
    }
}
