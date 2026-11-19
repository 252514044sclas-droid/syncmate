package com.example.syncmate.data.remote.dto

import com.google.gson.annotations.SerializedName

data class ScanListingRequestDto(
    @SerializedName("description") val description: String,
    @SerializedName("price") val price: Double,
    @SerializedName("image_urls") val imageUrls: List<String> = emptyList(),
    @SerializedName("title") val title: String? = null
)

data class RiskSignalDto(
    @SerializedName("type") val type: String,
    @SerializedName("description") val description: String,
    @SerializedName("severity") val severity: String
)

data class FakeListingAnalysisDto(
    @SerializedName("listing_id") val listingId: String? = null,
    @SerializedName("risk_score") val riskScore: Int,
    @SerializedName("risk_level") val riskLevel: String,
    @SerializedName("warning_signals") val warningSignals: List<RiskSignalDto> = emptyList(),
    @SerializedName("explanation") val explanation: String,
    @SerializedName("image_authenticity_score") val imageAuthenticityScore: Int = 90,
    @SerializedName("text_authenticity_score") val textAuthenticityScore: Int = 85,
    @SerializedName("price_deviation_percent") val priceDeviationPercent: Double = 0.0,
    @SerializedName("is_verified_backend_service") val isVerifiedBackendService: Boolean = true
)
