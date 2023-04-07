package com.example.weatherjourney.features.recommendation.data.repository

import com.example.weatherjourney.domain.AppPreferences
import com.example.weatherjourney.features.recommendation.data.mapper.toRecommendations
import com.example.weatherjourney.features.recommendation.data.remote.RecommendationApi
import com.example.weatherjourney.features.recommendation.domain.model.Recommendations
import com.example.weatherjourney.features.recommendation.domain.repository.RecommendationRepository
import com.example.weatherjourney.features.weather.domain.mapper.coordinate
import com.example.weatherjourney.util.Result
import com.example.weatherjourney.util.runCatching
import kotlinx.coroutines.flow.first

class DefaultRecommendationRepository(
    private val api: RecommendationApi,
    private val appPreferences: AppPreferences
) : RecommendationRepository {

    override suspend fun getRecommendations(): Result<Recommendations> {
        val location = appPreferences.locationPreferencesFlow.first()
        location.run {
            return when (
                val quality = runCatching {
                    api.getAirQuality(
                        latitude = coordinate.latitude,
                        longitude = coordinate.longitude,
                        timeZone = timeZone
                    )
                }
            ) {
                is Result.Success -> Result.Success(
                    quality.data.hourly.toRecommendations(timeZone)
                )

                is Result.Error -> quality
            }
        }
    }
}
