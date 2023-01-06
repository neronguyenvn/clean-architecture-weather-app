package com.example.weather.model.weather

import androidx.annotation.DrawableRes

data class CurrentWeather(
    val date: String,
    val temp: Int,
    val weather: String,
    @DrawableRes val bgImg: Int
)
