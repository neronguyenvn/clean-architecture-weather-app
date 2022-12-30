package com.example.weather.ui.screens

import android.app.Activity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.platform.LocalContext
import com.example.weather.utils.hasPermission

/**
 * Enum of Permission Action results
 */
enum class PermissionAction {
    GRANTED, DENIED
}

/**
 * Ui component for displaying Permission Request dialog with Custom permission and handled by Custom
 * Permission Action lambda passed in
 */
@Composable
fun PermissionScreen(
    permission: String,
    permissionAction: (PermissionAction) -> Unit
) {
    val activity = LocalContext.current as Activity
    val isPermissionGranted = activity.hasPermission(permission)

    if (isPermissionGranted) {
        permissionAction(PermissionAction.GRANTED)
        return
    }

    val requestPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            permissionAction(PermissionAction.GRANTED)
        } else {
            permissionAction(PermissionAction.DENIED)
        }
    }

    SideEffect {
        requestPermissionLauncher.launch(permission)
    }
}
