package com.example.weatherjourney.weather.data.repository

import kotlinx.coroutines.flow.MutableSharedFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DefaultRefreshRepository @Inject constructor() {
    val refreshFlow = MutableSharedFlow<Boolean>()
    suspend fun emit() = refreshFlow.emit(true)
}
