package com.example.weather.util

import com.example.weather.utils.Result

fun <T> checkResult(condition: Boolean, data: T): Result<T> {
    return if (condition) {
        Result.Success(data)
    } else {
        Result.Error(RuntimeException("Boom..."))
    }
}
