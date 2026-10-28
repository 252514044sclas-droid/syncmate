package com.example.syncmate.data.repository

import com.example.syncmate.data.model.FakeListingAnalysis
import com.example.syncmate.data.model.RiskSignal
import kotlinx.coroutines.delay

interface FakeListingRepository {
    suspend fun analyzeListingForFraud(description: String, price: Double, imageUrls: List<String>): Result<FakeListingAnalysis>
}

class MockFakeListingRepositoryImpl : FakeListingRepository {
    override suspend fun analyzeListingForFraud(
        description: String,
        price: Double,
        imageUrls: List<String>
    ): Result<FakeListingAnalysis> {
        delay(1400)

        val hasSuspiciousKeywords = description.contains("wire transfer", ignoreCase = true) ||
                description.contains("western union", ignoreCase = true) ||
                description.contains("out of country", ignoreCase = true) ||
                description.contains("cash app", ignoreCase = true) ||
                description.contains("advance before viewing", ignoreCase = true) ||
                description.contains("crypto", ignoreCase = true)

        val isUnusuallyLowPrice = price < 5000.0

        val warningSignals = mutableListOf<RiskSignal>()
        var riskScore = 8

        if (hasSuspiciousKeywords) {
            riskScore += 45
            warningSignals.add(
                RiskSignal(
                    type = "Payment Redirection Warning",
                    description = "Description references non-standard payment or advance before physical viewing.",
                    severity = "HIGH"
                )
            )
        }

        if (isUnusuallyLowPrice) {
            riskScore += 35
            warningSignals.add(
                RiskSignal(
                    type = "Unusual Price Anomaly",
                    description = "Price is significantly below average market rate for this student zone (₹4,000/mo vs ₹15,000/mo avg).",
                    severity = "HIGH"
                )
            )
        }

        if (description.length < 30) {
            riskScore += 15
            warningSignals.add(
                RiskSignal(
                    type = "Vague Listing Details",
                    description = "Short or incomplete description lacking key lease terms and address details.",
                    severity = "MEDIUM"
                )
            )
        }

        val riskLevel = when {
            riskScore >= 50 -> "HIGH_RISK"
            riskScore >= 25 -> "SUSPICIOUS"
            else -> "SAFE"
        }

        return Result.success(
            FakeListingAnalysis(
                riskScore = riskScore,
                riskLevel = riskLevel,
                warningSignals = warningSignals,
                explanation = if (riskScore < 25) {
                    "No major fraud indicators detected. Listing details match expected campus housing patterns."
                } else {
                    "Our AI Fake-Listing Shield identified potential risk indicators. Proceed with caution and verify student credentials before transferring funds."
                },
                imageAuthenticityScore = 92,
                textAuthenticityScore = if (hasSuspiciousKeywords) 45 else 88,
                priceDeviationPercent = if (isUnusuallyLowPrice) -55.0 else -3.5,
                isVerifiedBackendService = false
            )
        )
    }
}
