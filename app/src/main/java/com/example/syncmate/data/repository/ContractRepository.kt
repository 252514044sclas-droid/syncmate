package com.example.syncmate.data.repository

import android.util.Base64
import com.example.syncmate.data.model.ContractSummary
import com.example.syncmate.data.model.FlaggedClause
import com.example.syncmate.data.remote.ApiService
import com.example.syncmate.data.remote.dto.AnalyzeContractRequestDto
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

interface ContractRepository {
    suspend fun analyzeContractDocument(fileName: String, fileBytes: ByteArray? = null): Result<ContractSummary>
}

typealias ContractGuardRepository = ContractRepository

class NetworkContractRepositoryImpl(
    private val apiService: ApiService? = null
) : ContractRepository {

    override suspend fun analyzeContractDocument(
        fileName: String,
        fileBytes: ByteArray?
    ): Result<ContractSummary> = withContext(Dispatchers.IO) {
        if (apiService == null) {
            return@withContext Result.failure(Exception("Contract Guard network service not initialized"))
        }

        // Convert uploaded file bytes into Base64 for server-side PDF text extraction
        val base64Str = fileBytes?.let { bytes ->
            Base64.encodeToString(bytes, Base64.NO_WRAP)
        }

        // If file is plain text / markdown, also extract the raw string directly
        val rawTextContent = if (fileBytes != null && (fileName.endsWith(".txt", ignoreCase = true) || fileName.endsWith(".md", ignoreCase = true))) {
            try { String(fileBytes, Charsets.UTF_8) } catch (_: Exception) { null }
        } else null

        val request = AnalyzeContractRequestDto(
            fileName = fileName,
            fileBase64 = base64Str,
            contractText = rawTextContent
        )

        try {
            val response = apiService.analyzeContract(request)

            if (response.isSuccessful && response.body()?.data != null) {
                val data = response.body()!!.data!!
                val mappedClauses = data.flaggedClauses.map { clause ->
                    FlaggedClause(
                        title = clause.title.ifEmpty { "Flagged Clause" },
                        clauseText = clause.clauseText.ifEmpty { "Clause identified in agreement" },
                        severity = clause.severity.ifEmpty { "MEDIUM" },
                        explanation = clause.explanation.ifEmpty { "Review this clause carefully before signing." },
                        recommendation = clause.recommendation.ifEmpty { "Request clarification or modification from landlord." }
                    )
                }

                Result.success(
                    ContractSummary(
                        id = data.id,
                        fileName = data.fileName.ifEmpty { fileName },
                        leaseTerm = data.leaseTerm.ifEmpty { "11 Months" },
                        monthlyRent = data.monthlyRent,
                        securityDeposit = data.securityDeposit,
                        extraFees = data.extraFees,
                        utilityResponsibilities = data.utilityResponsibilities,
                        noticePeriodDays = data.noticePeriodDays,
                        subleaseAllowed = data.subleaseAllowed,
                        flaggedClauses = mappedClauses,
                        overallRiskScore = data.overallRiskScore,
                        summaryOverview = data.summaryOverview,
                        isAnalysisVerified = data.isAnalysisVerified
                    )
                )
            } else {
                val errorMsg = response.errorBody()?.string()
                    ?: response.body()?.message
                    ?: "Server returned HTTP ${response.code()}: Contract Guard analysis failed"
                Result.failure(Exception(errorMsg))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Cannot connect to Contract Guard server: ${e.localizedMessage ?: e.message}"))
        }
    }
}


