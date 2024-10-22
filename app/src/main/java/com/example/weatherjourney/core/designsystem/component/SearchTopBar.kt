package com.example.weatherjourney.core.designsystem.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.weatherjourney.R
import com.example.weatherjourney.core.designsystem.component.SearchTopBarAction.NoBack
import com.example.weatherjourney.core.designsystem.component.SearchTopBarAction.WithBack

sealed interface SearchTopBarAction {
    data class WithBack(val onBackClick: () -> Unit) : SearchTopBarAction
    data class NoBack(val onBarClick: () -> Unit) : SearchTopBarAction
}

@Composable
fun SearchTopBar(
    action: SearchTopBarAction,
    modifier: Modifier = Modifier,
    query: String = "",
    onQueryChange: (String) -> Unit = { /* No action */ },
) {
    val focusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    Column(modifier = modifier.clickable { if (action is NoBack) action.onBarClick() }) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 16.dp)
        ) {
            if (action is WithBack) {
                IconButton(onClick = action.onBackClick) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                        contentDescription = "Back",
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
            }
            OutlinedTextField(
                value = query,
                onValueChange = onQueryChange,
                singleLine = true,
                placeholder = { Text(text = stringResource(R.string.enter_location)) },
                colors = TextFieldDefaults.colors(
                    disabledContainerColor = Color.Transparent,
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                ),
                enabled = action is WithBack,
                modifier = Modifier
                    .weight(1f)
                    .focusRequester(focusRequester),
                keyboardActions = KeyboardActions(
                    onDone = { focusManager.clearFocus() },
                )
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        HorizontalDivider()
    }
}
