package com.example.weatherjourney.weather.presentation.setting.component

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.example.weatherjourney.R

@Composable
fun SettingTopBar(onBackClick: () -> Unit, modifier: Modifier = Modifier) {
    TopAppBar(
        navigationIcon = {
            IconButton(onBackClick) {
                Icon(
                    Icons.Filled.ArrowBack,
                    contentDescription = stringResource(R.string.back)
                )
            }
        },
        title = {
            Text(
                stringResource(R.string.setting),
                style = MaterialTheme.typography.titleMedium
            )
        },
        modifier = modifier
    )
}
