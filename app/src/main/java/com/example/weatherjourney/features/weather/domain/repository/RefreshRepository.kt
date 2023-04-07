package com.example.weatherjourney.features.weather.domain.repository

import androidx.work.WorkInfo
import kotlinx.coroutines.flow.Flow

interface RefreshRepository {

    val outputWorkInfo: Flow<WorkInfo>
    fun startWhenConnectivitySuccess()
}
