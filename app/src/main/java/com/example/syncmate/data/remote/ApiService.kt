package com.example.syncmate.data.remote

import com.example.syncmate.data.remote.dto.ApiResponse
import com.example.syncmate.data.remote.dto.AuthResponseDto
import com.example.syncmate.data.remote.dto.ContractSummaryDto
import com.example.syncmate.data.remote.dto.CreateListingRequestDto
import com.example.syncmate.data.remote.dto.DashboardSummaryDto
import com.example.syncmate.data.remote.dto.ForgotPasswordRequestDto
import com.example.syncmate.data.remote.dto.HousingListingDto
import com.example.syncmate.data.remote.dto.LoginRequestDto
import com.example.syncmate.data.remote.dto.PredictionRequestDto
import com.example.syncmate.data.remote.dto.PredictionResponseDto
import com.example.syncmate.data.remote.dto.RegisterRequestDto
import com.example.syncmate.data.remote.dto.ReportListingRequestDto
import com.example.syncmate.data.remote.dto.SaveListingRequestDto
import com.example.syncmate.data.remote.dto.UpdateProfileRequestDto
import com.example.syncmate.data.remote.dto.UserDto
import com.example.syncmate.data.remote.dto.VerifyEmailRequestDto
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Query

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

    @POST("ai/predict_rent.php")
    suspend fun predictRent(@Body request: PredictionRequestDto): Response<ApiResponse<PredictionResponseDto>>

    @Multipart
    @POST("ai/analyze_contract.php")
    suspend fun analyzeContract(
        @Part file: MultipartBody.Part
    ): Response<ApiResponse<ContractSummaryDto>>

    @GET("dashboard/summary.php")
    suspend fun getDashboardSummary(): Response<ApiResponse<DashboardSummaryDto>>
}
