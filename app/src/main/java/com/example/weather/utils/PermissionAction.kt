package com.example.weather.utils

/**
 * Sealed class for result of Permission Action.
 */
sealed class PermissionAction {

    /**
     * Object for result of Permission granted.
     */
    object OnPermissionGranted : PermissionAction()

    /**
     * Object for result of Permission denied.
     */
    object OnPermissionDenied : PermissionAction()
}
