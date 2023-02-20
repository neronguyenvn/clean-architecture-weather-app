package com.example.weatherjourney.weather.domain.usecase.weather

import com.example.weatherjourney.domain.PreferenceRepository
import com.example.weatherjourney.util.Result
import com.example.weatherjourney.weather.data.mapper.toWeatherAdviceState
import com.example.weatherjourney.weather.domain.repository.WeatherRepository
import com.example.weatherjourney.weather.presentation.notification.WeatherAdviceState

class GetWeatherAdvices(
    private val repository: WeatherRepository,
    private val preferences: PreferenceRepository
) {

    suspend operator fun invoke(): Result<WeatherAdviceState> {
        val timeZone = preferences.getLastTimeZone()

        return when (
            val quality = repository.getAirQuality(
                preferences.getLastCoordinate(),
                timeZone
            )
        ) {
            is Result.Success -> Result.Success(quality.data.hourly.toWeatherAdviceState(timeZone))
            is Result.Error -> quality
        }
    }
}
