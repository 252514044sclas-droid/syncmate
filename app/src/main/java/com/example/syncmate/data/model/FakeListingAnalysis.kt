package com.example.syncmate.data.model

data class RiskSignal(
    val type: String, // e.g. "Price Anomaly", "Duplicate Image", "Contact Redirection"
    val description: String,
    val severity: String // "HIGH", "MEDIUM", "INFO"
)

data class FakeListingAnalysis(
    val listingId: String? = null,
    val riskScore: Int, // 0 (Safe) to 100 (High Fraud Risk)
    val riskLevel: String, // "SAFE", "SUSPICIOUS", "HIGH_RISK"
    val warningSignals: List<RiskSignal>,
    val explanation: String,
    val imageAuthenticityScore: Int, // 0-100
    val textAuthenticityScore: Int, // 0-100
    val priceDeviationPercent: Double,
    val isVerifiedBackendService: Boolean = false
)
