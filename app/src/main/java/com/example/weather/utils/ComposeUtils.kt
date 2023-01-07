package com.example.weather.utils

import android.app.Activity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.example.weather.model.utils.PermissionAction

/**
 * Ui component for displaying Permission Request dialog with Custom permission and handled by Custom
 * Permission Action lambda passed in.
 */
@Composable
fun PermissionContent(
    permission: String,
    permissionAction: (PermissionAction) -> Unit
) {
    val activity = LocalContext.current as Activity
    val isPermissionGranted = activity.hasPermission(permission)

    if (isPermissionGranted) {
        permissionAction(PermissionAction.OnPermissionGranted)
        return
    }

    val requestPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            permissionAction(PermissionAction.OnPermissionGranted)
        } else {
            permissionAction(PermissionAction.OnPermissionDenied)
        }
    }

    SideEffect {
        requestPermissionLauncher.launch(permission)
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun LoadingContent(
    isLoading: Boolean,
    onRefresh: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    val pullRefreshState = rememberPullRefreshState(
        refreshing = isLoading,
        onRefresh = onRefresh
    )

    Box(modifier.pullRefresh(pullRefreshState)) {
        content()
        PullRefreshIndicator(
            isLoading,
            pullRefreshState,
            modifier = Modifier.align(Alignment.TopCenter)
        )
    }
}
