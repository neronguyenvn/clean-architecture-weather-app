package com.example.weatherjourney.weather.domain.mapper

import com.example.weatherjourney.weather.domain.model.Coordinate
import com.example.weatherjourney.weather.domain.model.CurrentWeather
import com.example.weatherjourney.weather.domain.model.SavedCity

fun CurrentWeather.toSavedCity(location: String, coordinate: Coordinate): SavedCity = SavedCity(
    weather = weather,
    temp = temp,
    imageUrl = imageUrl,
    location = location,
    coordinate = coordinate
)
