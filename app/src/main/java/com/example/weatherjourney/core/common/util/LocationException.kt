package com.example.weatherjourney.core.common.util

sealed class LocationException : Exception() {
    object LocationServiceDisabledException : LocationException()
    object LocationPermissionDeniedException : LocationException()
    object NullLastLocation : LocationException()
}
