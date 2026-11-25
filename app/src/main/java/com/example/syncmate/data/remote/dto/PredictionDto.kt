package com.example.syncmate.data.remote.dto

import com.google.gson.annotations.SerializedName

data class PredictionRequestDto(
    @SerializedName("location") val location: String = "Adyar, Chennai",
    @SerializedName("square_feet", alternate = ["area_sqft", "area"]) val squareFeet: Int = 600,
    @SerializedName("bedrooms") val bedrooms: Int = 1,
    @SerializedName("bathrooms") val bathrooms: Double = 1.0,
    @SerializedName("room_type") val roomType: String = "Private Room",
    @SerializedName("facilities") val facilities: List<String> = emptyList(),
    @SerializedName("lease_duration_months", alternate = ["lease_duration"]) val leaseDurationMonths: Int = 3,
    @SerializedName("season") val season: String = "Summer",
    @SerializedName("distance_km") val distanceKm: Double = 1.2,
    @SerializedName("has_ac") val hasAc: Boolean = false
)

data class PredictionResponseDto(
    @SerializedName("predicted_fair_rent", alternate = ["predicted_fair_price", "fair_price", "predicted_price", "rent", "predicted_rent_inr"]) 
    val predictedFairRent: Double,
    
    @SerializedName("rent_range_low", alternate = ["price_range_low", "range_low", "fair_range_min_inr", "range_min"]) 
    val rentRangeLow: Double,
    
    @SerializedName("rent_range_high", alternate = ["price_range_high", "range_high", "fair_range_max_inr", "range_max"]) 
    val rentRangeHigh: Double,
    
    @SerializedName("estimated_utilities_monthly", alternate = ["utilities_monthly", "utilities", "estimated_utilities_inr"]) 
    val estimatedUtilitiesMonthly: Double,
    
    @SerializedName("utility_breakdown") 
    val utilityBreakdown: Map<String, Double>? = emptyMap(),
    
    @SerializedName("price_assessment") 
    val priceAssessment: String? = "Fair Market Value (Based on Live ML Model)",
    
    @SerializedName("is_model_connected") 
    val isModelConnected: Boolean = true,
    
    @SerializedName("confidence_score") 
    val confidenceScore: Double = 0.95,
    
    @SerializedName("market_insights") 
    val marketInsights: List<String>? = emptyList(),

    @SerializedName("locality_tier")
    val localityTier: String? = null
)
