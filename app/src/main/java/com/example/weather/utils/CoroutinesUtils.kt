package com.example.weather.utils

import kotlinx.coroutines.flow.SharingStarted

private const val StopTimeoutMillis: Long = 5000

/**
 * A [SharingStarted] meant to be used with a StateFlow to expose data to the UI.
 */
val WhileUiSubscribed: SharingStarted = SharingStarted.WhileSubscribed(StopTimeoutMillis)
