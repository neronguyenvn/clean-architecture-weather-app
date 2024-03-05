package com.example.weatherjourney.core.data

import kotlinx.coroutines.flow.Flow

interface NetworkMonitor {

    fun observe(): Flow<Status>

    enum class Status {
        Available, Unavailable, Lost
    }
}
