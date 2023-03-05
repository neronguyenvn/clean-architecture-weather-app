package com.example.weatherjourney.util

import com.example.weatherjourney.R

data class UserMessage(val message: UiText, val actionLabel: ActionLabel = ActionLabel.NULL)

enum class ActionLabel(val label: UiText) {
    ADD(UiText.StringResource(R.string.add)),
    DELETE(UiText.StringResource(R.string.delete)),
    NULL(UiText.DynamicString(""))
}
