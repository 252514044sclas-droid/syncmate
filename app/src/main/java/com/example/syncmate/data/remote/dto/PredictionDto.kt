package com.example.syncmate.data.remote.dto

import com.google.gson.annotations.SerializedName

data class PredictionRequestDto(
    @SerializedName("location") val location: String,
    @SerializedName("square_feet") val squareFeet: Int,
    @SerializedName("bedrooms") val bedrooms: Int,
    @SerializedName("bathrooms") val bathrooms: Double,
    @SerializedName("room_type") val roomType: String,
    @SerializedName("facilities") val facilities: List<String>,
    @SerializedName("lease_duration_months") val leaseDurationMonths: Int,
    @SerializedName("season") val season: String
)

data class PredictionResponseDto(
    @SerializedName("predicted_fair_rent") val predictedFairRent: Double,
    @SerializedName("rent_range_low") val rentRangeLow: Double,
    @SerializedName("rent_range_high") val rentRangeHigh: Double,
    @SerializedName("estimated_utilities_monthly") val estimatedUtilitiesMonthly: Double,
    @SerializedName("utility_breakdown") val utilityBreakdown: Map<String, Double>,
    @SerializedName("price_assessment") val priceAssessment: String,
    @SerializedName("is_model_connected") val isModelConnected: Boolean = true,
    @SerializedName("confidence_score") val confidenceScore: Double = 0.92,
    @SerializedName("market_insights") val marketInsights: List<String> = emptyList()
)
