package com.example.weatherjourney.presentation

import android.util.Log
import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherjourney.R
import com.example.weatherjourney.util.ActionLabel
import com.example.weatherjourney.util.Result
import com.example.weatherjourney.util.UiText
import com.example.weatherjourney.util.UserMessage
import com.example.weatherjourney.weather.domain.repository.RefreshRepository
import com.example.weatherjourney.weather.util.DELAY_TIME
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.net.UnknownHostException

abstract class BaseViewModel(
    private val refreshRepository: RefreshRepository
) : ViewModel() {

    protected val _userMessage: MutableStateFlow<UserMessage?> = MutableStateFlow(null)
    protected val _isLoading = MutableStateFlow(false)

    private var listenSuccessNetworkJob: Job? = null

    abstract fun onRefresh()

    fun onSnackbarMessageShown() {
        _userMessage.value = null
    }

    fun onClearListenJob() {
        listenSuccessNetworkJob = null
    }

    protected fun showSnackbarMessage(
        @StringRes messageResId: Int,
        actionLabel: ActionLabel = ActionLabel.NULL,
        vararg arg: Any
    ) {
        _userMessage.value = UserMessage(UiText.StringResource(messageResId, arg.toList()), actionLabel)
    }

    protected open fun onRefresh(vararg functions: suspend () -> Unit) {
        _isLoading.value = true
        viewModelScope.launch {
            val job = launch {
                functions.forEach { launch { it() } }

                // If functions completed before DELAY_TIME, onRefresh will be delay a little bit
                // for more real
                launch { delay(DELAY_TIME) }
            }

            job.join()
            _isLoading.value = false
        }
    }

    protected fun handleErrorResult(result: Result.Error, refresh: () -> Unit = { onRefresh() }) {
        Log.d(this.javaClass.simpleName, result.toString())

        val messageId = when (result.exception) {
            is UnknownHostException -> {
                refreshRepository.startListenWhenConnectivitySuccess()

                if (listenSuccessNetworkJob == null) {
                    listenSuccessNetworkJob = viewModelScope.launch {
                        refreshRepository.outputWorkInfo.first { it.state.isFinished }
                            .let {
                                showSnackbarMessage(R.string.restore_internet_connection)
                                refresh()
                            }
                    }
                }

                R.string.no_internet_connection
            }

            else -> R.string.something_went_wrong
        }

        showSnackbarMessage(messageId)
    }
}
