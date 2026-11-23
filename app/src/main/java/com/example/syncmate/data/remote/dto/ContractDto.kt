package com.example.syncmate.data.remote.dto

import com.google.gson.annotations.SerializedName

data class AnalyzeContractRequestDto(
    @SerializedName("file_name") val fileName: String,
    @SerializedName("file_base64") val fileBase64: String? = null,
    @SerializedName("contract_text") val contractText: String? = null
)

data class FlaggedClauseDto(
    @SerializedName("title", alternate = ["clause_title", "name"])
    val title: String = "Flagged Clause",

    @SerializedName("clause_text", alternate = ["clauseText", "raw_clause"])
    val clauseText: String = "",

    @SerializedName("severity")
    val severity: String = "MEDIUM",

    @SerializedName("explanation", alternate = ["description", "reason"])
    val explanation: String = "",

    @SerializedName("recommendation", alternate = ["advice", "suggestion"])
    val recommendation: String = ""
)

data class ContractSummaryDto(
    @SerializedName("id")
    val id: String = "",

    @SerializedName("file_name")
    val fileName: String = "",

    @SerializedName("lease_term")
    val leaseTerm: String = "11 months",

    @SerializedName("monthly_rent")
    val monthlyRent: Double = 0.0,

    @SerializedName("security_deposit")
    val securityDeposit: Double = 0.0,

    @SerializedName("extra_fees")
    val extraFees: Map<String, Double> = emptyMap(),

    @SerializedName("utility_responsibilities")
    val utilityResponsibilities: List<String> = emptyList(),

    @SerializedName("notice_period_days")
    val noticePeriodDays: Int = 30,

    @SerializedName("sublease_allowed")
    val subleaseAllowed: Boolean = true,

    @SerializedName("flagged_clauses")
    val flaggedClauses: List<FlaggedClauseDto> = emptyList(),

    @SerializedName("overall_risk_score")
    val overallRiskScore: Int = 20,

    @SerializedName("summary_overview")
    val summaryOverview: String = "",

    @SerializedName("is_analysis_verified")
    val isAnalysisVerified: Boolean = true
)
