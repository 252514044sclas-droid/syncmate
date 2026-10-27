package com.example.syncmate.data.model

data class FlaggedClause(
    val title: String,
    val clauseText: String,
    val severity: String, // "HIGH", "MEDIUM", "LOW"
    val explanation: String,
    val recommendation: String
)

data class ContractSummary(
    val id: String,
    val fileName: String,
    val leaseTerm: String,
    val monthlyRent: Double,
    val securityDeposit: Double,
    val extraFees: Map<String, Double>, // e.g. "Move-in fee" -> 150.0
    val utilityResponsibilities: List<String>,
    val noticePeriodDays: Int,
    val subleaseAllowed: Boolean,
    val flaggedClauses: List<FlaggedClause>,
    val overallRiskScore: Int, // 0 to 100
    val summaryOverview: String,
    val isAnalysisVerified: Boolean = false
)
