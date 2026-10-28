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

        // Machine learning model simulation in Indian Rupees (INR)
        val baseRentPerSqFt = 20.0
        val baseRent = (request.squareFeet * baseRentPerSqFt) + (request.bedrooms * 2500.0) + (request.bathrooms * 1500.0)
        val roomTypeMultiplier = when (request.roomType) {
            "Entire Studio" -> 1.25
            "Entire Apartment" -> 1.40
            "Private Room" -> 0.85
            else -> 0.65
        }
        val predictedRent = (baseRent * roomTypeMultiplier).coerceIn(8000.0, 28000.0)
        val lowRent = (predictedRent * 0.92)
        val highRent = (predictedRent * 1.08)

        // Monthly utilities in INR (₹800 - ₹3,000)
        val estimatedUtilities = (1200.0 + (request.bedrooms * 350.0) + if (request.facilities.contains("AC")) 600.0 else 0.0)
            .coerceIn(800.0, 3000.0)

        val utilityBreakdown = mapOf(
            "Electricity & AC" to (estimatedUtilities * 0.45),
            "Water & Maintenance" to (estimatedUtilities * 0.25),
            "Society & Waste Management" to (estimatedUtilities * 0.15),
            "High-Speed Broadband" to (estimatedUtilities * 0.15)
        )

        return Result.success(
            RentPredictionResult(
                predictedFairRent = String.format("%.0f", predictedRent).toDouble(),
                rentRangeLow = String.format("%.0f", lowRent).toDouble(),
                rentRangeHigh = String.format("%.0f", highRent).toDouble(),
                estimatedUtilitiesMonthly = String.format("%.0f", estimatedUtilities).toDouble(),
                utilityBreakdown = utilityBreakdown,
                priceAssessment = "Fair Market Estimate (Based on ML Student Housing Model)",
                isModelConnected = false,
                confidenceScore = 0.91,
                marketInsights = listOf(
                    "Sublets in this campus quadrant are currently 8% lower during summer semester break.",
                    "Including utilities and broadband in rent saves an estimated ₹1,800/month.",
                    "Walking distance to campus metro station increases demand by ~10%.",
                    "Note: AI predictions are sample data benchmarks for student planning and should be confirmed with the landlord."
                )
            )
        )
    }
}
