package com.example.syncmate.data.remote.dto

import com.google.gson.annotations.SerializedName

data class SyncQuizRequestDto(
    @SerializedName("user_id") val userId: String,
    @SerializedName("name") val name: String,
    @SerializedName("university") val university: String,
    @SerializedName("preferences") val preferences: Map<String, Any?>
)

data class RemoteUserDto(
    @SerializedName("id") val id: String,
    @SerializedName("name") val name: String,
    @SerializedName("email") val email: String?,
    @SerializedName("university") val university: String,
    @SerializedName("is_verified") val isVerified: Boolean = true,
    @SerializedName("bio") val bio: String = "",
    @SerializedName("preferred_lease_term") val preferredLeaseTerm: String = "Summer 2025",
    @SerializedName("max_budget") val maxBudget: Double = 15000.0,
    @SerializedName("preferred_location") val preferredLocation: String = "Near Campus",
    @SerializedName("has_completed_quiz") val hasCompletedQuiz: Boolean = true,
    @SerializedName("quiz_preferences") val quizPreferences: Map<String, Any?>? = null
)

data class SendRoommateRequestDto(
    @SerializedName("request_id") val requestId: String,
    @SerializedName("sender_id") val senderId: String,
    @SerializedName("sender_name") val senderName: String,
    @SerializedName("sender_university") val senderUniversity: String,
    @SerializedName("receiver_id") val receiverId: String,
    @SerializedName("receiver_name") val receiverName: String,
    @SerializedName("receiver_university") val receiverUniversity: String = "",
    @SerializedName("compatibility_score") val compatibilityScore: Int,
    @SerializedName("intro_message") val introMessage: String = ""
)

data class RoommateRequestDto(
    @SerializedName("request_id") val requestId: String,
    @SerializedName("sender_id") val senderId: String,
    @SerializedName("sender_name") val senderName: String,
    @SerializedName("sender_university") val senderUniversity: String = "",
    @SerializedName("receiver_id") val receiverId: String,
    @SerializedName("receiver_name") val receiverName: String,
    @SerializedName("receiver_university") val receiverUniversity: String = "",
    @SerializedName("compatibility_score") val compatibilityScore: Int = 0,
    @SerializedName("intro_message") val introMessage: String? = null,
    @SerializedName("status") val status: String = "PENDING",
    @SerializedName("timestamp") val timestamp: Long = 0L
)

data class RequestsBundleDto(
    @SerializedName("incoming") val incoming: List<RoommateRequestDto> = emptyList(),
    @SerializedName("sent") val sent: List<RoommateRequestDto> = emptyList()
)

data class UpdateRequestStatusDto(
    @SerializedName("request_id") val requestId: String,
    @SerializedName("status") val status: String
)
