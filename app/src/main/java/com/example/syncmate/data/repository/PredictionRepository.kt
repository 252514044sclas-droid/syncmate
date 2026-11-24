package com.example.syncmate.data.repository

import com.example.syncmate.data.model.RentPredictionRequest
import com.example.syncmate.data.model.RentPredictionResult
import com.example.syncmate.data.remote.ApiService
import com.example.syncmate.data.remote.dto.PredictionRequestDto
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

interface PredictionRepository {
    suspend fun predictFairRentAndUtilities(request: RentPredictionRequest): Result<RentPredictionResult>
}

typealias FairPriceRepository = PredictionRepository

class NetworkPredictionRepositoryImpl(
    private val apiService: ApiService? = null
) : PredictionRepository {

    override suspend fun predictFairRentAndUtilities(request: RentPredictionRequest): Result<RentPredictionResult> = withContext(Dispatchers.IO) {
        if (apiService == null) {
            return@withContext Result.failure(Exception("Fair-Price network service not initialized"))
        }

        val reqDto = PredictionRequestDto(
            location = request.location,
            squareFeet = request.squareFeet,
            bedrooms = request.bedrooms,
            bathrooms = request.bathrooms,
            roomType = request.roomType,
            facilities = request.facilities,
            leaseDurationMonths = request.leaseDurationMonths,
            season = request.season,
            distanceKm = 1.2,
            hasAc = request.facilities.any { it.contains("AC", ignoreCase = true) }
        )

        try {
            val res = apiService.predictRent(reqDto)

            if (res.isSuccessful && res.body()?.data != null) {
                val data = res.body()!!.data!!
                Result.success(
                    RentPredictionResult(
                        predictedFairRent = data.predictedFairRent,
                        rentRangeLow = data.rentRangeLow,
                        rentRangeHigh = data.rentRangeHigh,
                        estimatedUtilitiesMonthly = data.estimatedUtilitiesMonthly,
                        utilityBreakdown = data.utilityBreakdown,
                        priceAssessment = data.priceAssessment,
                        isModelConnected = data.isModelConnected,
                        confidenceScore = data.confidenceScore,
                        marketInsights = data.marketInsights,
                        localityTier = data.localityTier
                    )
                )
            } else {
                val errorMsg = res.errorBody()?.string()
                    ?: res.body()?.message
                    ?: "Server returned HTTP ${res.code()}: Failed to compute live fair rent"
                Result.failure(Exception(errorMsg))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Cannot connect to Fair-Price server: ${e.localizedMessage ?: e.message}"))
        }
    }
}

// Backward-compatible alias for existing instantiations
typealias MockPredictionRepositoryImpl = NetworkPredictionRepositoryImpl
