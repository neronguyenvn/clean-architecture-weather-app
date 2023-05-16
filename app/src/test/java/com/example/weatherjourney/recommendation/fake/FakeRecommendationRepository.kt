package com.example.weatherjourney.recommendation.fake

import com.example.weatherjourney.features.recommendation.domain.model.Recommendations
import com.example.weatherjourney.features.recommendation.domain.repository.RecommendationRepository
import com.example.weatherjourney.recommendation.recommendations1
import com.example.weatherjourney.util.Result

class FakeRecommendationRepository : RecommendationRepository {

    var isSuccessful: Boolean = true
    var recommendations: Recommendations? = null
    var exception: Exception = RuntimeException("BOOM!!")

    override suspend fun getRecommendations(): Result<Recommendations> {
        return if (isSuccessful) {
            Result.Success(recommendations ?: recommendations1)
        } else {
            Result.Error(exception)
        }
    }
}
