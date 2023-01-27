package com.example.weatherjourney.util

import androidx.annotation.StringRes

sealed class UiEvent {
    data class ShowSnackbar(
        val message: UiText,
        @StringRes val actionLabel: Int = 0
    ) : UiEvent()

    object StartWithSearchRoute : UiEvent()
}
