package com.example.weatherjourney.fake

import com.example.weatherjourney.core.data.NetworkMonitor
import com.example.weatherjourney.core.data.NetworkMonitor.Status
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.filterNotNull

class FakeNetworkMonitor : NetworkMonitor {
    private val statusFlow = MutableStateFlow<Status?>(null)

    fun setStatus(status: Status) {
        statusFlow.value = status
    }

    override fun observe(): Flow<Status> {
        return statusFlow.filterNotNull()
    }
}
