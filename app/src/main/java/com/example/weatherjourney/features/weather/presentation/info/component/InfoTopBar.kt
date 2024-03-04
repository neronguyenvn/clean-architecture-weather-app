package com.example.weatherjourney.features.weather.presentation.info.component

import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import com.example.weatherjourney.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InfoTopBar(
    cityAddress: String,
    onSearchClick: () -> Unit,
    onSettingClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    CenterAlignedTopAppBar(
        modifier = modifier,
        navigationIcon = {
            IconButton(onSearchClick) {
                Icon(
                    painter = painterResource(R.drawable.ic_search),
                    contentDescription = stringResource(R.string.search),
                )
            }
        },
        title = {
            Text(
                cityAddress,
                style = MaterialTheme.typography.titleMedium,
                maxLines = 1,
                textAlign = TextAlign.Center,
            )
        },
        actions = {
            IconButton(onSettingClick) {
                Icon(
                    painter = painterResource(R.drawable.ic_setting),
                    contentDescription = stringResource(R.string.setting),
                    tint = MaterialTheme.colorScheme.onSurface,
                )
            }
        },
    )
}
