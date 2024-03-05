package com.example.weatherjourney.util

import com.example.weatherjourney.core.common.util.Result

fun <T> checkResult(condition: Boolean, data: T): Result<T> {
    return if (condition) {
        Result.Success(data)
    } else {
        Result.Error(RuntimeException("Boom..."))
    }
}
