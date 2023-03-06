package com.example.weatherjourney.weather.presentation.notification

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.example.weatherjourney.R
import com.example.weatherjourney.presentation.BaseViewModel
import com.example.weatherjourney.util.Async
import com.example.weatherjourney.util.Result
import com.example.weatherjourney.util.UiText
import com.example.weatherjourney.util.UiText.DynamicString
import com.example.weatherjourney.util.UserMessage
import com.example.weatherjourney.util.WhileUiSubscribed
import com.example.weatherjourney.weather.domain.repository.RefreshRepository
import com.example.weatherjourney.weather.domain.usecase.WeatherUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val TAG = "WeatherNotificationViewModel"

@HiltViewModel
class WeatherNotificationViewModel @Inject constructor(
    private val weatherUseCases: WeatherUseCases,
    private val refreshRepository: RefreshRepository
) : BaseViewModel() {

    private val _notificationsAsync = flow { emit(weatherUseCases.getWeatherAdvices()) }
        .map { handleResult(it) }
        .onStart { Async.Loading }

    val uiState: StateFlow<WeatherNotificationUiState> = combine(
        _userMessage,
        _isLoading,
        _notificationsAsync
    ) { userMessage, isLoading, notificationsAsync ->

        when (notificationsAsync) {
            Async.Loading -> WeatherNotificationUiState(isLoading = true)
            is Async.Success -> {
                WeatherNotificationUiState(
                    notifications = notificationsAsync.data,
                    isLoading = isLoading,
                    userMessage = userMessage
                )
            }
        }
    }.stateIn(
        scope = viewModelScope,
        started = WhileUiSubscribed,
        initialValue = WeatherNotificationUiState(isLoading = true)
    )

    fun refresh() = viewModelScope.launch {
        runSuspend(
            launch { _notificationsAsync.first() },
            launch { delay(1500) }
        )
    }

    private fun handleResult(result: Result<WeatherNotifications>): Async<WeatherNotifications?> =
        if (result is Result.Success) {
            Async.Success(result.data)
        } else {
            val message = result.toString()
            showSnackbarMessage(UserMessage(DynamicString(message)))
            Log.e(TAG, message)

            refreshRepository.startListenWhenConnectivitySuccess()

            if (listenSuccessNetworkJob != null) {
                Async.Success(null)
            }

            listenSuccessNetworkJob = viewModelScope.launch {
                refreshRepository.outputWorkInfo.collect { info ->
                    if (info.state.isFinished) {
                        showSnackbarMessage(UserMessage(UiText.StringResource(R.string.restore_internet_connection)))
                        refresh()
                    }
                }
            }

            Async.Success(null)
        }
}
