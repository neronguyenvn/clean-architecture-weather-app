package com.example.weatherjourney.weather.domain.usecase

import com.example.weatherjourney.di.IoDispatcher
import com.example.weatherjourney.domain.PreferenceRepository
import com.example.weatherjourney.util.Result
import com.example.weatherjourney.weather.data.remote.dto.AllWeather
import com.example.weatherjourney.weather.domain.model.Coordinate
import com.example.weatherjourney.weather.domain.repository.WeatherRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class GetAllWeatherAndCacheLastInfo(
    private val repository: WeatherRepository,
    private val preferences: PreferenceRepository,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) {

    suspend operator fun invoke(
        coordinate: Coordinate,
        timeZone: String,
        forceCache: Boolean
    ): Result<AllWeather> =
        when (
            val weather = repository.fetchAllWeather(
                coordinate,
                timeZone,
                preferences.getTemperatureUnit().apiParam,
                preferences.getWindSpeedUnit().apiParam,
                forceCache
            )
        ) {
            is Result.Success -> {
                withContext(ioDispatcher) {
                    launch {
                        preferences.saveCoordinate(coordinate)
                        preferences.saveTimeZone(timeZone)
                    }
                }
                weather
            }

            else -> weather
        }
}
