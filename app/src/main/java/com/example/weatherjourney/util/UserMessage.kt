package com.example.weatherjourney.util

import androidx.annotation.StringRes
import com.example.weatherjourney.R

open class UserMessage(val message: UiText? = null, @StringRes val actionLabel: Int? = null) {
    object AddingLocation :
        UserMessage(UiText.StringResource(R.string.add_this_location), R.string.add)

    class DeletingLocation(cityAddress: String) :
        UserMessage(
            UiText.StringResource(R.string.delete_location, listOf(cityAddress)),
            R.string.delete
        )

    object RequestingLocationPermission : UserMessage()

    object RequestingTurnOnLocationService : UserMessage()
}
