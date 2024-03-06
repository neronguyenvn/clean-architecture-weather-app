package com.example.weatherjourney.core.common.util

import android.util.Log
import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherjourney.R
import com.example.weatherjourney.core.common.constant.DELAY_TIME
import com.example.weatherjourney.core.data.NetworkMonitor
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.net.UnknownHostException

abstract class ViewModeWithMessageAndLoading(
    private val networkMonitor: NetworkMonitor,
) : ViewModel() {

    protected val userMessage: MutableStateFlow<UserMessage?> = MutableStateFlow(null)
    protected val isLoading = MutableStateFlow(false)
    private var refreshJob: Job? = null

    abstract fun onRefresh()

    fun onHandleUserMessageDone() {
        userMessage.value = null
    }

    protected fun showSnackbarMessage(
        @StringRes messageResId: Int,
        @StringRes actionLabel: Int? = null,
        vararg arg: Any,
    ) {
        userMessage.value =
            UserMessage(UiText.StringResource(messageResId, arg.toList()), actionLabel)
    }

    protected open fun runSuspend(vararg functions: suspend () -> Unit) {
        isLoading.value = true
        refreshJob?.cancel()
        refreshJob = viewModelScope.launch {
            val job = launch {
                functions.forEach { launch { it() } }
                launch { delay(DELAY_TIME) }
            }

            job.join()
            isLoading.value = false
        }
    }

    protected fun handleErrorResult(
        result: Result.Error,
        shouldShowErrorMessage: Boolean = true,
    ) {
        Log.d(this.javaClass.simpleName, result.toString())
        val messageId = when (result.exception) {
            is UnknownHostException -> {
                refreshJob?.cancel()
                isLoading.value = false
                viewModelScope.launch {
                    networkMonitor.observe()
                        .first { it == NetworkMonitor.Status.Available }
                        .let {
                            onRefresh()
                            showSnackbarMessage(R.string.restore_internet_connection)
                        }
                }
                R.string.no_internet_connection
            }

            else -> R.string.something_went_wrong
        }
        if (shouldShowErrorMessage) showSnackbarMessage(messageId)
    }
}