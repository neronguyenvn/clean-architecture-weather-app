package com.example.weatherjourney.features.recommendation.domain.repository

import com.example.weatherjourney.features.recommendation.domain.model.Recommendations
import com.example.weatherjourney.util.Result

interface RecommendationRepository {

    suspend fun getRecommendations(): Result<Recommendations>
}
