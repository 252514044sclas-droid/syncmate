package com.example.syncmate

import com.example.syncmate.data.model.RentPredictionRequest
import com.example.syncmate.data.repository.NetworkContractRepositoryImpl
import com.example.syncmate.data.repository.NetworkPredictionRepositoryImpl
import com.example.syncmate.data.repository.NetworkFakeListingRepositoryImpl
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class AiMicroserviceResilienceTest {

    @Test
    fun `contract repository succeeds and detects predatory clauses even when backend is offline`() = runBlocking {
        // apiService = null simulates complete server refusal / offline mode
        val repository = NetworkContractRepositoryImpl(apiService = null)

        val scamAgreement = """
            RESIDENTIAL STUDENT SUBLEASE AGREEMENT
            1. Monthly rent shall be Rs. 24,000 payable on 1st of each month.
            2. Security deposit of Rs. 96,000 is required. The security deposit shall be entirely non-refundable under any circumstance.
            3. The landlord is currently stationed overseas; tenant agrees to wire transfer token advance of Rs. 15,000 via GooglePay/UPI before scheduling physical inspection of the property.
            4. In the event of rent delay exceeding 24 hours, lessor reserves the right to immediately change locks, disconnect electricity, and seize tenant belongings.
            5. Notice period: Lessor may evict tenant on 24 hours notice without assigning reasons.
            6. Tenant agrees to waive all statutory rights, protections, and recourse provided under the Model Tenancy Act.
        """.trimIndent()

        val result = repository.analyzeContractDocument(
            fileName = "Predatory_Lease_Trap.txt",
            fileBytes = scamAgreement.toByteArray(Charsets.UTF_8)
        )

        assertTrue("Analysis must succeed even when offline without throwing network failure", result.isSuccess)
        val summary = result.getOrNull()
        assertNotNull(summary)
        assertTrue("Predatory contract should have high risk score", summary!!.overallRiskScore >= 70)
        assertTrue("Should detect at least 2 predatory clauses", summary.flaggedClauses.size >= 2)
        assertTrue("Should detect non-refundable deposit trap", summary.flaggedClauses.any { it.title.contains("Non-Refundable", ignoreCase = true) })
        assertTrue("Should detect eviction lockout risk", summary.flaggedClauses.any { it.title.contains("Eviction", ignoreCase = true) || it.title.contains("Lockout", ignoreCase = true) })
    }

    @Test
    fun `contract repository succeeds and validates standard agreement when offline`() = runBlocking {
        val repository = NetworkContractRepositoryImpl(apiService = null)

        val standardAgreement = """
            STANDARD RESIDENTIAL TENANCY AGREEMENT
            1. The monthly rent for the demised premises shall be Rs. 18,500 payable on or before 5th of each calendar month.
            2. The tenant has deposited a sum of Rs. 37,000 as refundable security deposit with the lessor.
            3. The security deposit shall be refunded in full within 30 days of vacating.
            4. Either party may terminate this agreement by giving 30 days prior written notice.
        """.trimIndent()

        val result = repository.analyzeContractDocument(
            fileName = "Standard_Tenancy.txt",
            fileBytes = standardAgreement.toByteArray(Charsets.UTF_8)
        )

        assertTrue(result.isSuccess)
        val summary = result.getOrNull()
        assertNotNull(summary)
        assertTrue("Standard contract should have low risk score", summary!!.overallRiskScore < 40)
        assertEquals(30, summary.noticePeriodDays)
    }

    @Test
    fun `fair rent repository computes calibrated prediction when backend is offline`() = runBlocking {
        val repository = NetworkPredictionRepositoryImpl(apiService = null)

        val request = RentPredictionRequest(
            location = "Adyar, Chennai",
            squareFeet = 650,
            bedrooms = 1,
            bathrooms = 1.0,
            roomType = "1BHK",
            facilities = listOf("WiFi", "AC", "Power Backup"),
            leaseDurationMonths = 6,
            season = "Summer"
        )

        val result = repository.predictFairRentAndUtilities(request)

        assertTrue("Fair rent must succeed without throwing connection error", result.isSuccess)
        val prediction = result.getOrNull()
        assertNotNull(prediction)
        assertTrue("Predicted rent should be in realistic range for 1BHK in Chennai", prediction!!.predictedFairRent in 10000.0..30000.0)
        assertTrue("Utilities estimate must be positive", prediction.estimatedUtilitiesMonthly > 0)
        assertTrue("Confidence score should be high", prediction.confidenceScore >= 0.90)
        assertNotNull(prediction.localityTier)
    }

    @Test
    fun `fake listing repository computes fraud signals when backend is offline`() {
        val analysis = NetworkFakeListingRepositoryImpl.computeLocalFraudAnalysis(
            description = "Luxury 3BHK flat near university. Wire token advance via GooglePay before visit as landlord is overseas.",
            price = 3000.0,
            imageUrls = emptyList()
        )

        assertTrue("Scam listing should be marked HIGH risk", analysis.riskScore >= 70)
        assertEquals("HIGH", analysis.riskLevel)
        assertTrue("Should detect price anomaly", analysis.warningSignals.any { it.type == "PRICE_ANOMALY" })
        assertTrue("Should detect advance fee trap", analysis.warningSignals.any { it.type == "ADVANCE_FEE_TRAP" })
    }
}

