package com.example.syncmate.data.remote.dto

import com.google.gson.annotations.SerializedName

data class RoommateProfileDto(
    @SerializedName("id") val id: String,
    @SerializedName("name") val name: String,
    @SerializedName("university") val university: String,
    @SerializedName("avatar_url") val avatarUrl: String? = null,
    @SerializedName("compatibility_percentage") val compatibilityPercentage: Int,
    @SerializedName("match_reason") val matchReason: String,
    @SerializedName("lifestyle_rhythm") val lifestyleRhythm: String,
    @SerializedName("max_budget_monthly") val maxBudgetMonthly: Double,
    @SerializedName("preferred_location") val preferredLocation: String,
    @SerializedName("is_saved") val isSaved: Boolean = false,
    @SerializedName("bio") val bio: String = ""
)

data class DashboardSummaryDto(
    @SerializedName("user_name") val userName: String,
    @SerializedName("unread_notifications_count") val unreadNotificationsCount: Int,
    @SerializedName("unread_messages_count") val unreadMessagesCount: Int,
    @SerializedName("recommended_listings") val recommendedListings: List<HousingListingDto>,
    @SerializedName("roommate_matches") val roommateMatches: List<RoommateProfileDto>
)
