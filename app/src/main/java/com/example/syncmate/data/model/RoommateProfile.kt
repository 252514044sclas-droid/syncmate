package com.example.syncmate.data.model

data class RoommateProfile(
    val id: String,
    val name: String,
    val university: String,
    val avatarUrl: String? = null,
    val compatibilityPercentage: Int = 85,
    val matchReason: String = "",
    val lifestyleRhythm: String = "",
    val maxBudgetMonthly: Double = 15000.0,
    val preferredLocation: String = "",
    val isSaved: Boolean = false,
    val bio: String = ""
)

data class RoommateMatchProfile(
    val user: User,
    val compatibilityPercentage: Int = 85,
    val matchReason: String = "",
    val whyYouMatch: List<String> = emptyList(),
    val dimensionScores: Map<String, Int> = emptyMap(),
    val requestStatus: String = "NONE", // NONE, PENDING_SENT, PENDING_RECEIVED, ACCEPTED, DECLINED
    val requestId: String? = null
)
