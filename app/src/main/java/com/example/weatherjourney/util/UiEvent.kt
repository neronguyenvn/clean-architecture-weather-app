package com.example.weatherjourney.util

sealed class UiEvent {
    data class ShowSnackbar(val message: UiText) : UiEvent()
}
