package com.example.weatherjourney.features.recommendation.presentation

import androidx.lifecycle.viewModelScope
import com.example.weatherjourney.domain.ConnectivityObserver
import com.example.weatherjourney.features.recommendation.domain.model.Recommendations
import com.example.weatherjourney.features.recommendation.domain.repository.RecommendationRepository
import com.example.weatherjourney.presentation.ViewModeWithMessageAndLoading
import com.example.weatherjourney.util.Async
import com.example.weatherjourney.util.Result
import com.example.weatherjourney.util.UserMessage
import com.example.weatherjourney.util.WhileUiSubscribed
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

data class RecommendationUiState(
    val recommendations: Recommendations? = null,
    val isLoading: Boolean = false,
    val userMessage: UserMessage? = null
)

@HiltViewModel
class RecommendationViewModel @Inject constructor(
    private val recommendationRepository: RecommendationRepository,
    connectivityObserver: ConnectivityObserver
) : ViewModeWithMessageAndLoading(connectivityObserver) {

    private val _notificationsAsync = MutableStateFlow<Async<Recommendations?>>(Async.Loading)

    val uiState: StateFlow<RecommendationUiState> = combine(
        _userMessage,
        _isLoading,
        _notificationsAsync
    ) { userMessage, isLoading, notificationsAsync ->

        when (notificationsAsync) {
            Async.Loading -> RecommendationUiState(isLoading = true)
            is Async.Success -> {
                RecommendationUiState(
                    recommendations = notificationsAsync.data,
                    isLoading = isLoading,
                    userMessage = userMessage
                )
            }
        }
    }.stateIn(
        scope = viewModelScope,
        started = WhileUiSubscribed,
        initialValue = RecommendationUiState(isLoading = true)
    )

    init {
        onRefresh()
    }

    override fun onRefresh() = super.runSuspend({
        val notifications = recommendationRepository.getRecommendations()
        _notificationsAsync.value = handleResult(notifications)
    })

    private fun handleResult(result: Result<Recommendations>): Async<Recommendations?> =
        when (result) {
            is Result.Success -> Async.Success(result.data)
            is Result.Error -> {
                handleErrorResult(result)
                if (_notificationsAsync.value is Async.Loading) {
                    Async.Success(null)
                } else {
                    _notificationsAsync.value
                }
            }
        }
}
