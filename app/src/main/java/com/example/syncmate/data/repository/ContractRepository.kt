package com.example.syncmate.data.repository

import com.example.syncmate.data.model.ContractSummary
import com.example.syncmate.data.model.FlaggedClause
import kotlinx.coroutines.delay

interface ContractRepository {
    suspend fun analyzeContractDocument(fileName: String, fileBytes: ByteArray? = null): Result<ContractSummary>
}

class MockContractRepositoryImpl : ContractRepository {
    override suspend fun analyzeContractDocument(
        fileName: String,
        fileBytes: ByteArray?
    ): Result<ContractSummary> {
        delay(1800)

        val flaggedClauses = listOf(
            FlaggedClause(
                title = "Automatic Lease Renewal Without Prior Notice",
                clauseText = "Clause 14B: Lease shall automatically renew for a full 12-month term unless written notice is received 90 days prior to expiration.",
                severity = "HIGH",
                explanation = "A 90-day notice window is unusually long for student sublets and may trap you into an unwanted term.",
                recommendation = "Request shortening the notice window to 30 days or removing auto-renewal for subleases."
            ),
            FlaggedClause(
                title = "Subletter Cleaning Fee Deduction",
                clauseText = "Clause 8: Mandatory non-refundable $250 cleaning fee deducted directly from security deposit upon move-out.",
                severity = "MEDIUM",
                explanation = "Non-refundable deposit deductions must be explicitly agreed upon upfront.",
                recommendation = "Clarify whether deep cleaning is required or if standard broom-clean state waives the fee."
            )
        )

        return Result.success(
            ContractSummary(
                id = "ctr_" + System.currentTimeMillis(),
                fileName = fileName,
                leaseTerm = "May 15, 2025 - August 15, 2025",
                monthlyRent = 850.0,
                securityDeposit = 500.0,
                extraFees = mapOf(
                    "Sublease Transfer Fee" to 75.0,
                    "Utility Setup Fee" to 25.0
                ),
                utilityResponsibilities = listOf(
                    "Tenant pays Electricity and Gas",
                    "Landlord covers Water, Sewer, Trash, and High-Speed Internet"
                ),
                noticePeriodDays = 60,
                subleaseAllowed = true,
                flaggedClauses = flaggedClauses,
                overallRiskScore = 28,
                summaryOverview = "Contract Guard completed legal clause analysis. Standard sublease agreement with 2 potential risk flags flagged for your review.",
                isAnalysisVerified = false
            )
        )
    }
}
