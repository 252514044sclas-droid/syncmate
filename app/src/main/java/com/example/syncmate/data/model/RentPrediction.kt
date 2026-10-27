package com.example.syncmate.data.model

data class RentPredictionRequest(
    val location: String,
    val squareFeet: Int,
    val bedrooms: Int,
    val bathrooms: Double,
    val roomType: String,
    val facilities: List<String>,
    val leaseDurationMonths: Int,
    val season: String
)

data class RentPredictionResult(
    val predictedFairRent: Double,
    val rentRangeLow: Double,
    val rentRangeHigh: Double,
    val estimatedUtilitiesMonthly: Double,
    val utilityBreakdown: Map<String, Double>, // e.g., "Electricity" -> 45.0
    val priceAssessment: String, // e.g., "Fair Market Value", "Overpriced by 12%", "Great Deal"
    val isModelConnected: Boolean = false,
    val confidenceScore: Double = 0.92,
    val marketInsights: List<String> = emptyList()
)
