package com.example.weatherjourney.weather.domain.model

data class SuggestionCity(
    val countryFlag: String,
    val formattedLocationString: String,
    val latitude: Double,
    val longitude: Double
)
