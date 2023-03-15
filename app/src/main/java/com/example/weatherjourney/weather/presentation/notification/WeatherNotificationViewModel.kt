package com.example.weatherjourney.weather.presentation.notification

import androidx.lifecycle.viewModelScope
import com.example.weatherjourney.presentation.BaseViewModel
import com.example.weatherjourney.util.Async
import com.example.weatherjourney.util.Result
import com.example.weatherjourney.util.WhileUiSubscribed
import com.example.weatherjourney.weather.domain.repository.RefreshRepository
import com.example.weatherjourney.weather.domain.usecase.WeatherUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class WeatherNotificationViewModel @Inject constructor(
    private val weatherUseCases: WeatherUseCases,
    refreshRepository: RefreshRepository
) : BaseViewModel(refreshRepository) {

    private val _notificationsAsync = MutableStateFlow<Async<WeatherNotifications?>>(Async.Loading)

    init {
        onRefresh()
    }

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

    override fun onRefresh() = super.runSuspend({
        val notifications = weatherUseCases.getWeatherNotifications()
        _notificationsAsync.value = handleResult(notifications)
    })

    private fun handleResult(result: Result<WeatherNotifications>): Async<WeatherNotifications?> =
        when (result) {
            is Result.Success -> Async.Success(result.data)
            is Result.Error -> {
                handleErrorResult(result)
                Async.Success(null)
            }
        }
}
