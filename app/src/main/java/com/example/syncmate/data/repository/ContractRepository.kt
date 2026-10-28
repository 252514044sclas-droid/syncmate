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
                clauseText = "Clause 14B: Lease shall automatically renew for a full 11-month term unless written notice is received 90 days prior to expiration.",
                severity = "HIGH",
                explanation = "A 90-day notice window is unusually long for student sublets and may trap you into an unwanted term.",
                recommendation = "Request shortening the notice window to 30 days or removing auto-renewal for student subleases."
            ),
            FlaggedClause(
                title = "Mandatory Cleaning Fee Deduction",
                clauseText = "Clause 8: Mandatory non-refundable ₹2,500 cleaning fee deducted directly from security deposit upon move-out.",
                severity = "MEDIUM",
                explanation = "Non-refundable security deposit deductions must be explicitly agreed upon upfront under standard tenancy terms.",
                recommendation = "Clarify whether deep cleaning is required or if returning the room in broom-clean state waives the ₹2,500 deduction."
            )
        )

        return Result.success(
            ContractSummary(
                id = "ctr_" + System.currentTimeMillis(),
                fileName = fileName,
                leaseTerm = "May 15, 2025 - August 15, 2025",
                monthlyRent = 14500.0,
                securityDeposit = 25000.0,
                extraFees = mapOf(
                    "Sublease Transfer Fee" to 1500.0,
                    "Utility & Society Setup" to 500.0
                ),
                utilityResponsibilities = listOf(
                    "Tenant pays Electricity and Cooking Gas",
                    "Landlord covers Water, Society Maintenance, and High-Speed Broadband"
                ),
                noticePeriodDays = 60,
                subleaseAllowed = true,
                flaggedClauses = flaggedClauses,
                overallRiskScore = 28,
                summaryOverview = "Contract Guard completed legal clause analysis. Standard sublease agreement with 2 potential risk flags flagged for student review.",
                isAnalysisVerified = false
            )
        )
    }
}
