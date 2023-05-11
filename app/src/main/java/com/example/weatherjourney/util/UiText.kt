package com.example.weatherjourney.util

import android.content.Context
import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import java.util.Objects

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
                    context.getString(id, args.toTypedArray())
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
                    stringResource(id, args.toTypedArray())
                }
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (this.javaClass != other?.javaClass) return false

        return when (this) {
            is DynamicString -> other is DynamicString && this.text == other.text
            is StringResource -> other is StringResource && this.id == other.id && this.args == other.args
        }
    }

    override fun hashCode() = when (this) {
        is DynamicString -> Objects.hash(this.text)
        is StringResource -> Objects.hash(this.id, this.args)
    }
}
