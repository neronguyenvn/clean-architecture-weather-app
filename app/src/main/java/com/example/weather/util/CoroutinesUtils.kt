package com.example.weather.util

import kotlinx.coroutines.flow.SharingStarted

/**
 * A [SharingStarted] meant to be used with a StateFlow to expose data to the UI.
 */
val WhileUiSubscribed: SharingStarted = SharingStarted.WhileSubscribed(StopTimeoutMillis)
