package com.example.syncmate.data.remote

import com.example.syncmate.data.remote.dto.AnalyzeContractRequestDto
import com.example.syncmate.data.remote.dto.ApiResponse
import com.example.syncmate.data.remote.dto.AuthResponseDto
import com.example.syncmate.data.remote.dto.ChatRequestDto
import com.example.syncmate.data.remote.dto.ChatResponseDto
import com.example.syncmate.data.remote.dto.ContractSummaryDto
import com.example.syncmate.data.remote.dto.CreateListingRequestDto
import com.example.syncmate.data.remote.dto.DashboardSummaryDto
import com.example.syncmate.data.remote.dto.FakeListingAnalysisDto
import com.example.syncmate.data.remote.dto.ForgotPasswordRequestDto
import com.example.syncmate.data.remote.dto.HousingListingDto
import com.example.syncmate.data.remote.dto.LoginRequestDto
import com.example.syncmate.data.remote.dto.PredictionRequestDto
import com.example.syncmate.data.remote.dto.PredictionResponseDto
import com.example.syncmate.data.remote.dto.RegisterRequestDto
import com.example.syncmate.data.remote.dto.ReportListingRequestDto
import com.example.syncmate.data.remote.dto.RoommateProfileDto
import com.example.syncmate.data.remote.dto.SaveListingRequestDto
import com.example.syncmate.data.remote.dto.ScanListingRequestDto
import com.example.syncmate.data.remote.dto.UpdateProfileRequestDto
import com.example.syncmate.data.remote.dto.UserDto
import com.example.syncmate.data.remote.dto.VerifyEmailRequestDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

typealias SyncMateApiService = ApiService

interface ApiService {
    @POST("auth/login.php")
    suspend fun login(@Body request: LoginRequestDto): Response<ApiResponse<AuthResponseDto>>

    @POST("auth/register.php")
    suspend fun register(@Body request: RegisterRequestDto): Response<ApiResponse<AuthResponseDto>>

    @POST("auth/verify_email.php")
    suspend fun verifyEmail(@Body request: VerifyEmailRequestDto): Response<ApiResponse<Boolean>>

    @POST("auth/forgot_password.php")
    suspend fun forgotPassword(@Body request: ForgotPasswordRequestDto): Response<ApiResponse<Boolean>>

    @GET("user/profile.php")
    suspend fun getUserProfile(): Response<ApiResponse<UserDto>>

    @POST("user/update_profile.php")
    suspend fun updateUserProfile(@Body request: UpdateProfileRequestDto): Response<ApiResponse<UserDto>>

    @GET("listings/featured.php")
    suspend fun getFeaturedListings(): Response<ApiResponse<List<HousingListingDto>>>

    @GET("listings/search.php")
    suspend fun searchListings(
        @Query("q") query: String? = null,
        @Query("max_price") maxPrice: Double? = null,
        @Query("room_type") roomType: String? = null
    ): Response<ApiResponse<List<HousingListingDto>>>

    @GET("listings/detail.php")
    suspend fun getListingDetail(@Query("id") id: String): Response<ApiResponse<HousingListingDto>>

    @POST("listings/create.php")
    suspend fun createListing(@Body request: CreateListingRequestDto): Response<ApiResponse<HousingListingDto>>

    @POST("listings/toggle_save.php")
    suspend fun toggleSaveListing(@Body request: SaveListingRequestDto): Response<ApiResponse<Boolean>>

    @GET("listings/saved.php")
    suspend fun getSavedListings(): Response<ApiResponse<List<HousingListingDto>>>

    @POST("listings/report.php")
    suspend fun reportListing(@Body request: ReportListingRequestDto): Response<ApiResponse<Boolean>>

    // AI Module 1: Fair-Price & Utility Regressor (Live ML Model)
    @POST("predict_price")
    suspend fun predictPrice(@Body request: PredictionRequestDto): Response<ApiResponse<PredictionResponseDto>>

    @POST("ai/predict_rent.php")
    suspend fun predictRent(@Body request: PredictionRequestDto): Response<ApiResponse<PredictionResponseDto>>

    // AI Module 2: Fake-Listing Scam Shield (Live ML Scanner)
    @POST("scan_listing")
    suspend fun scanListing(@Body request: ScanListingRequestDto): Response<ApiResponse<FakeListingAnalysisDto>>

    // AI Module 3: Contract Guard (Live ML Document Analyzer)
    @POST("ai/analyze_contract.php")
    suspend fun analyzeContract(@Body request: AnalyzeContractRequestDto): Response<ApiResponse<ContractSummaryDto>>

    // AI Module 4: Roommate Compatibility Matcher
    @GET("roommates/matches.php")
    suspend fun getRoommateMatches(): Response<ApiResponse<List<RoommateProfileDto>>>

    @POST("predict_compatibility")
    suspend fun predictCompatibility(@Body request: com.example.syncmate.data.remote.dto.PredictCompatibilityRequestDto): Response<ApiResponse<com.example.syncmate.data.remote.dto.CompatibilityResultDto>>

    @POST("roommates/sync_quiz")
    suspend fun syncQuizPreferences(@Body request: com.example.syncmate.data.remote.dto.SyncQuizRequestDto): Response<ApiResponse<Unit>>

    @GET("roommates/active_users")
    suspend fun getActiveQuizUsers(@Query("user_id") userId: String): Response<ApiResponse<List<com.example.syncmate.data.remote.dto.RemoteUserDto>>>

    @POST("roommates/send_request")
    suspend fun sendRoommateRequest(@Body request: com.example.syncmate.data.remote.dto.SendRoommateRequestDto): Response<ApiResponse<com.example.syncmate.data.remote.dto.RoommateRequestDto>>

    @GET("roommates/get_requests")
    suspend fun getRoommateRequests(@Query("user_id") userId: String): Response<ApiResponse<com.example.syncmate.data.remote.dto.RequestsBundleDto>>

    @POST("roommates/update_request")
    suspend fun updateRequestStatus(@Body request: com.example.syncmate.data.remote.dto.UpdateRequestStatusDto): Response<ApiResponse<Unit>>

    // AI Chatbot Assistant: Dynamic Conversational AI Integration
    @POST("ai/chat.php")
    suspend fun sendChatMessage(@Body request: ChatRequestDto): Response<ApiResponse<ChatResponseDto>>

    @GET("dashboard/summary.php")
    suspend fun getDashboardSummary(): Response<ApiResponse<DashboardSummaryDto>>
}
