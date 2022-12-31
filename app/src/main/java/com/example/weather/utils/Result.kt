/*
 * Copyright (C) 2019 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.weather.utils

/**
 * A generic class that holds a value or error.
 */
sealed class Result<out R> {

    /**
     * Generic class for hold a Value of Success Result
     */
    data class Success<out T>(val data: T) : Result<T>()

    /**
     * Generic class for hold an Exception of Error Result
     */
    data class Error(val exception: Exception) : Result<Nothing>()

    /**
     * Convert a Result into String
     */
    override fun toString(): String {
        return when (this) {
            is Success<*> -> "Success[data=$data]"
            is Error -> "Error[exception=$exception]"
        }
    }
}
