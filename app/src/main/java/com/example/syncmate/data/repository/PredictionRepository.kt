package com.example.syncmate.data.repository

import com.example.syncmate.data.model.RentPredictionRequest
import com.example.syncmate.data.model.RentPredictionResult
import kotlinx.coroutines.delay

interface PredictionRepository {
    suspend fun predictFairRentAndUtilities(request: RentPredictionRequest): Result<RentPredictionResult>
}

class MockPredictionRepositoryImpl : PredictionRepository {
    override suspend fun predictFairRentAndUtilities(request: RentPredictionRequest): Result<RentPredictionResult> {
        delay(1200)

        // Mock machine learning model output calculations
        val baseRentPerSqFt = 1.65
        val baseRent = (request.squareFeet * baseRentPerSqFt) + (request.bedrooms * 120.0) + (request.bathrooms * 80.0)
        val roomTypeMultiplier = when (request.roomType) {
            "Entire Studio" -> 1.2
            "Entire Apartment" -> 1.35
            "Private Room" -> 0.85
            else -> 0.65
        }
        val predictedRent = (baseRent * roomTypeMultiplier).coerceIn(450.0, 3500.0)
        val lowRent = (predictedRent * 0.92)
        val highRent = (predictedRent * 1.08)

        val estimatedUtilities = 65.0 + (request.bedrooms * 25.0) + if (request.facilities.contains("AC")) 20.0 else 0.0

        val utilityBreakdown = mapOf(
            "Electricity & AC" to (estimatedUtilities * 0.45),
            "Water & Sewer" to (estimatedUtilities * 0.25),
            "Trash & Recycling" to (estimatedUtilities * 0.15),
            "High-Speed Internet" to (estimatedUtilities * 0.15)
        )

        return Result.success(
            RentPredictionResult(
                predictedFairRent = String.format("%.2f", predictedRent).toDouble(),
                rentRangeLow = String.format("%.2f", lowRent).toDouble(),
                rentRangeHigh = String.format("%.2f", highRent).toDouble(),
                estimatedUtilitiesMonthly = String.format("%.2f", estimatedUtilities).toDouble(),
                utilityBreakdown = utilityBreakdown,
                priceAssessment = "Fair Market Estimate (Based on ML Scikit-Learn Housing Model)",
                isModelConnected = false, // Set false to indicate Python backend is ready to be connected via API
                confidenceScore = 0.91,
                marketInsights = listOf(
                    "Sublets in this quadrant are currently 5% cheaper during summer months.",
                    "Including utilities in rent saves an estimated $90/month.",
                    "Walking distance to Engineering library increases value by ~8%."
                )
            )
        )
    }
}
