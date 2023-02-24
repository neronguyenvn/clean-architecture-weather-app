package com.example.weatherjourney.presentation

import androidx.lifecycle.ViewModel
import com.example.weatherjourney.util.UserMessage
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow

abstract class BaseViewModel : ViewModel() {

    protected val _userMessage: MutableStateFlow<UserMessage?> = MutableStateFlow(null)
    protected val _isLoading = MutableStateFlow(false)

    fun snackbarMessageShown() {
        _userMessage.value = null
    }

    protected fun showSnackbarMessage(message: UserMessage) {
        _userMessage.value = message
    }

    protected suspend fun runSuspend(vararg jobs: Job) {
        _isLoading.value = true
        jobs.forEach { it.join() }
        _isLoading.value = false
    }
}
