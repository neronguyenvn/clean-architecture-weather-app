package com.example.weatherjourney.util

import android.content.Context
import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource

sealed class UiText {

    data class DynamicString(val text: String) : UiText()

    data class StringResource(@StringRes val id: Int, val args: List<Any> = emptyList()) : UiText()

    fun asString(context: Context): String {
        return when (this) {
            is DynamicString -> text
            is StringResource ->
                if (args.isEmpty()) {
                    context.getString(id)
                } else {
                    context.getString(id, *args.toTypedArray())
                }
        }
    }

    @Composable
    fun asString(): String {
        return when (this) {
            is DynamicString -> text
            is StringResource ->
                if (args.isEmpty()) {
                    stringResource(id)
                } else {
                    stringResource(id, *args.toTypedArray())
                }
        }
    }
}
