package com.example.weatherjourney.weather.presentation.notification

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherjourney.util.Result
import com.example.weatherjourney.util.UiEvent
import com.example.weatherjourney.util.UiText
import com.example.weatherjourney.weather.domain.usecase.WeatherUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val TAG = "WeatherNotificationViewModel"

@HiltViewModel
class WeatherNotificationViewModel @Inject constructor(
    private val weatherUseCases: WeatherUseCases
) : ViewModel() {

    var uiState by mutableStateOf(WeatherNotificationUiState())
        private set

    private val _uiEvent = MutableSharedFlow<UiEvent>()
    val uiEvent = _uiEvent.asSharedFlow()

    init {
        refresh(false)
    }

    fun onEvent(event: WeatherNotificationEvent) {
        when (event) {
            is WeatherNotificationEvent.OnRefresh -> refresh(true)
        }
    }

    private fun refresh(isDelay: Boolean) = viewModelScope.launch {
        runSuspend(
            viewModelScope.launch {
                when (val advices = weatherUseCases.getWeatherAdvices()) {
                    is Result.Success -> {
                        uiState = uiState.copy(weatherNotificationState = advices.data)
                    }

                    is Result.Error -> handleError(advices)
                }
            },

            if (isDelay) {
                launch {
                    delay(1500)
                }
            } else {
                launch { }
            }
        )
    }

    private suspend fun handleError(error: Result.Error) {
        val message = error.toString()
        Log.e(TAG, message)
        _uiEvent.emit(UiEvent.ShowSnackbar(UiText.DynamicString(message)))
    }

    private suspend fun runSuspend(vararg jobs: Job) {
        uiState = uiState.copy(isLoading = true)
        jobs.forEach { it.join() }
        uiState = uiState.copy(isLoading = false)
    }
}
