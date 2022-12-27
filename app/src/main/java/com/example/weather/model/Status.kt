package com.example.weather.model

import kotlinx.serialization.Serializable

@Serializable
data class Status(
    val code: Int,
    val message: String
)