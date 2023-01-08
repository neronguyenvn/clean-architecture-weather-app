package com.example.weather.model.ui

import androidx.annotation.DrawableRes

/**
 * Ui Model for Daily Weather DataType.
 */
data class CurrentWeather(
    val date: String,
    val temp: Int,
    val weather: String,
    @DrawableRes val bgImg: Int
)

/**
 * Ui Model for Daily Weather DataType.
 */
data class DailyWeather(
    val iconUrl: String,
    val date: String,
    val weather: String,
    val maxTemp: Int,
    val minTemp: Int
)
