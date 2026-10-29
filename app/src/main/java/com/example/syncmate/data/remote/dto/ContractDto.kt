package com.example.syncmate.data.remote.dto

import com.google.gson.annotations.SerializedName

data class FlaggedClauseDto(
    @SerializedName("title") val title: String,
    @SerializedName("clause_text") val clauseText: String,
    @SerializedName("severity") val severity: String,
    @SerializedName("explanation") val explanation: String,
    @SerializedName("recommendation") val recommendation: String
)

data class ContractSummaryDto(
    @SerializedName("id") val id: String,
    @SerializedName("file_name") val fileName: String,
    @SerializedName("lease_term") val leaseTerm: String,
    @SerializedName("monthly_rent") val monthlyRent: Double,
    @SerializedName("security_deposit") val securityDeposit: Double,
    @SerializedName("extra_fees") val extraFees: Map<String, Double> = emptyMap(),
    @SerializedName("utility_responsibilities") val utilityResponsibilities: List<String> = emptyList(),
    @SerializedName("notice_period_days") val noticePeriodDays: Int = 30,
    @SerializedName("sublease_allowed") val subleaseAllowed: Boolean = true,
    @SerializedName("flagged_clauses") val flaggedClauses: List<FlaggedClauseDto> = emptyList(),
    @SerializedName("overall_risk_score") val overallRiskScore: Int = 20,
    @SerializedName("summary_overview") val summaryOverview: String = "",
    @SerializedName("is_analysis_verified") val isAnalysisVerified: Boolean = true
)
