package com.example.syncmate

import com.example.syncmate.data.remote.dto.ContractSummaryDto
import com.example.syncmate.data.remote.dto.FlaggedClauseDto
import com.example.syncmate.data.remote.dto.PredictionResponseDto
import com.google.gson.Gson
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ResponseParsingTest {

    private val gson = Gson()

    @Test
    fun `fair rent response dto parses primary and alternate alias fields`() {
        val json = """
        {
            "predicted_rent_inr": 18500.0,
            "fair_range_min_inr": 16650.0,
            "fair_range_max_inr": 20350.0,
            "estimated_utilities_inr": 1650.0,
            "utility_breakdown": {
                "Electricity & AC": 742.0,
                "Water & Maintenance": 412.0,
                "High-Speed Broadband": 248.0
            },
            "market_insights": [
                "Anna Nagar is classified as Tier 1",
                "Rent calibrated to 2026 Chennai standards"
            ],
            "locality_tier": "Tier 1 (Prime Urban)"
        }
        """.trimIndent()

        val parsed = gson.fromJson(json, PredictionResponseDto::class.java)

        assertEquals(18500.0, parsed.predictedFairRent, 0.01)
        assertEquals(16650.0, parsed.rentRangeLow, 0.01)
        assertEquals(20350.0, parsed.rentRangeHigh, 0.01)
        assertEquals(1650.0, parsed.estimatedUtilitiesMonthly, 0.01)
        assertEquals(3, parsed.utilityBreakdown?.size ?: 0)
        assertEquals(2, parsed.marketInsights?.size ?: 0)
        assertEquals("Tier 1 (Prime Urban)", parsed.localityTier)
    }

    @Test
    fun `contract summary dto parses predatory scam clauses and risk score`() {
        val json = """
        {
            "id": "contract_abc_123",
            "file_name": "Anna_Nagar_Lease.pdf",
            "lease_term": "11 Months",
            "monthly_rent": 14000.0,
            "security_deposit": 28000.0,
            "notice_period_days": 30,
            "sublease_allowed": true,
            "overall_risk_score": 85,
            "summary_overview": "High risk contract with arbitrary forfeiture clauses.",
            "is_analysis_verified": true,
            "flagged_clauses": [
                {
                    "title": "Non-Refundable Deposit Trap",
                    "clause_text": "Security deposit shall not be refunded under any condition.",
                    "severity": "CRITICAL",
                    "explanation": "Security deposits must legally be refundable under Indian tenancy laws.",
                    "recommendation": "Demand explicit refundable deposit clause before signing."
                }
            ]
        }
        """.trimIndent()

        val parsed = gson.fromJson(json, ContractSummaryDto::class.java)

        assertEquals("contract_abc_123", parsed.id)
        assertEquals(14000.0, parsed.monthlyRent, 0.01)
        assertEquals(28000.0, parsed.securityDeposit, 0.01)
        assertEquals(85, parsed.overallRiskScore)
        assertTrue(parsed.isAnalysisVerified)
        assertEquals(1, parsed.flaggedClauses.size)

        val clause = parsed.flaggedClauses.first()
        assertEquals("Non-Refundable Deposit Trap", clause.title)
        assertEquals("CRITICAL", clause.severity)
    }

    @Test
    fun `parsing handles empty or missing optional fields with safe defaults`() {
        val minimalJson = """
        {
            "predicted_fair_rent": 12000.0,
            "rent_range_low": 10500.0,
            "rent_range_high": 13500.0,
            "estimated_utilities_monthly": 1200.0,
            "is_model_connected": true,
            "confidence_score": 0.95
        }
        """.trimIndent()

        val parsed = gson.fromJson(minimalJson, PredictionResponseDto::class.java)

        assertEquals(12000.0, parsed.predictedFairRent, 0.01)
        assertTrue(parsed.utilityBreakdown.isNullOrEmpty())
        assertTrue(parsed.marketInsights.isNullOrEmpty())
        assertTrue(parsed.isModelConnected)
        assertEquals(0.95, parsed.confidenceScore, 0.01)
    }
}
