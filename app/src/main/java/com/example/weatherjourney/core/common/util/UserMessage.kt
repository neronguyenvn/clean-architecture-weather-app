package com.example.weatherjourney.core.common.util

import androidx.annotation.StringRes
import com.example.weatherjourney.R
import java.util.Objects

open class UserMessage(val message: UiText? = null, @StringRes val actionLabel: Int? = null) {
    object AddingLocation :
        UserMessage(UiText.StringResource(R.string.add_this_location), R.string.add)

    class DeletingLocation(cityAddress: String) :
        UserMessage(
            UiText.StringResource(R.string.delete_location, listOf(cityAddress)),
            R.string.delete,
        )

    object RequestingLocationPermission : UserMessage()

    object RequestingTurnOnLocationService : UserMessage()

    override fun equals(other: Any?): Boolean {
        if (this === other) return true // check if identical
        if (javaClass != other?.javaClass) return false // check if same class and cast

        other as UserMessage

        if (message != other.message) return false
        if (actionLabel != other.actionLabel) return false

        return true // all properties are equal
    }

    override fun hashCode() = Objects.hash(message, actionLabel)
}
