package com.example.weatherjourney.weather.domain.usecase.weather

import com.example.weatherjourney.domain.PreferenceRepository
import com.example.weatherjourney.util.Result
import com.example.weatherjourney.weather.data.mapper.toWeatherAdviceState
import com.example.weatherjourney.weather.domain.mapper.toCoordinate
import com.example.weatherjourney.weather.domain.repository.WeatherRepository
import com.example.weatherjourney.weather.presentation.notification.WeatherNotifications
import kotlinx.coroutines.flow.first

class GetWeatherAdvices(
    private val repository: WeatherRepository,
    private val preferences: PreferenceRepository
) {

    suspend operator fun invoke(): Result<WeatherNotifications> {
        val location = preferences.locationPreferencesFlow.first()

        return when (
            val quality = repository.getAirQuality(
                location.coordinate.toCoordinate(),
                location.timeZone
            )
        ) {
            is Result.Success -> Result.Success(
                quality.data.hourly.toWeatherAdviceState(
                    location.timeZone
                )
            )

            is Result.Error -> quality
        }
    }
}
