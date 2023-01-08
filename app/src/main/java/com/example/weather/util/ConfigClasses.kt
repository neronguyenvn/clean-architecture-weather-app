package com.example.weather.util


/**
 * A generic class that holds a value or error.
 */
sealed class Result<out R> {

    /**
     * Generic class for hold a Value of Result.Success Result.
     */
    data class Success<out T>(val data: T) : Result<T>()

    /**
     * Generic class for hold an Exception of Result.Error Result.
     */
    data class Error(val exception: Exception) : Result<Nothing>()

    /**
     * Convert a Result into String.
     */
    override fun toString(): String {
        return when (this) {
            is Success<*> -> "Result.Success[data=$data]"
            is Error -> "Result.Error[exception=$exception]"
        }
    }
}

/**
 * Sealed class for result of Permission Action.
 */
sealed class PermissionAction {

    /**
     * Object for result of Permission granted.
     */
    object OnPermissionGranted : PermissionAction()

    /**
     * Object for result of Permission denied.
     */
    object OnPermissionDenied : PermissionAction()
}
