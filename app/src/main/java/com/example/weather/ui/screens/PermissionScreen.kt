package com.example.weather.ui.screens

import android.app.Activity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.platform.LocalContext
import com.example.weather.utils.PermissionAction
import com.example.weather.utils.hasPermission

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
        (permissionAction(PermissionAction.OnPermissionGranted))
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
