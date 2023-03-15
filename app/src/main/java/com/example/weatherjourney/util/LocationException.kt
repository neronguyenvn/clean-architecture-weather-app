package com.example.weatherjourney.util

sealed class LocationException : Exception() {
    object LocationServiceDisabledException : LocationException()
    object LocationPermissionDeniedException : LocationException()
    object NullLastLocation : LocationException()
}
