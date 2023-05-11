package com.example.weatherjourney.features.weather.presentation.setting.component

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.example.weatherjourney.presentation.component.SegmentText
import com.example.weatherjourney.presentation.component.SegmentedControl

@Composable
fun UnitItem(
    @StringRes title: Int,
    segments: List<String>,
    selectedSegment: String,
    onSegmentSelected: (String) -> Unit,
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(
            stringResource(title),
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.weight(1f),
        )
        SegmentedControl(
            segments,
            selectedSegment,
            onSegmentSelected = { onSegmentSelected(it) },
            modifier = Modifier.weight(1f),
        ) {
            SegmentText(it)
        }
    }
}
