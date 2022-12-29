package com.example.weather.ui.screens

import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat

sealed class PermissionAction {
    object OnPermissionGranted : PermissionAction()
    object OnPermissionDenied : PermissionAction()
}

@Composable
fun PermissionScreen(
    permission: String,
    permissionAction: (PermissionAction) -> Unit,
) {
    val context = LocalContext.current
    val isPermissionGranted = (ContextCompat.checkSelfPermission(
        context, permission
    ) == PackageManager.PERMISSION_GRANTED)

    if (isPermissionGranted) {
        permissionAction(PermissionAction.OnPermissionGranted)
        return
    }

    val requestPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
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