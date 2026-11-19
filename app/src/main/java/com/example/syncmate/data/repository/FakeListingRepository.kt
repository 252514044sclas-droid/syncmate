package com.example.syncmate.data.repository

import com.example.syncmate.data.model.FakeListingAnalysis
import com.example.syncmate.data.model.RiskSignal
import com.example.syncmate.data.remote.ApiService
import com.example.syncmate.data.remote.dto.ScanListingRequestDto
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

interface FakeListingRepository {
    suspend fun analyzeListingForFraud(description: String, price: Double, imageUrls: List<String>): Result<FakeListingAnalysis>
}

typealias ScamShieldRepository = FakeListingRepository

class NetworkFakeListingRepositoryImpl(
    private val apiService: ApiService
) : FakeListingRepository {
    override suspend fun analyzeListingForFraud(
        description: String,
        price: Double,
        imageUrls: List<String>
    ): Result<FakeListingAnalysis> = withContext(Dispatchers.IO) {
        val request = ScanListingRequestDto(
            description = description,
            price = price,
            imageUrls = imageUrls
        )

        try {
            val response = apiService.scanListing(request)

            if (response.isSuccessful && response.body()?.data != null) {
                val data = response.body()!!.data!!
                val mappedSignals = data.warningSignals.map { sig ->
                    RiskSignal(
                        type = sig.type,
                        description = sig.description,
                        severity = sig.severity
                    )
                }

                Result.success(
                    FakeListingAnalysis(
                        listingId = data.listingId,
                        riskScore = data.riskScore,
                        riskLevel = data.riskLevel,
                        warningSignals = mappedSignals,
                        explanation = data.explanation,
                        imageAuthenticityScore = data.imageAuthenticityScore,
                        textAuthenticityScore = data.textAuthenticityScore,
                        priceDeviationPercent = data.priceDeviationPercent,
                        isVerifiedBackendService = data.isVerifiedBackendService
                    )
                )
            } else {
                val errorMsg = response.errorBody()?.string() 
                    ?: response.body()?.message 
                    ?: "Server returned HTTP ${response.code()}: Scam Shield analysis failed"
                Result.failure(Exception(errorMsg))
            }
        } catch (e: Exception) {
            // Real network or parsing error exposed directly to UI
            Result.failure(Exception("Cannot connect to Scam Shield server: ${e.localizedMessage ?: e.message}"))
        }
    }
}

// Backward-compatible alias
typealias MockFakeListingRepositoryImpl = NetworkFakeListingRepositoryImpl
