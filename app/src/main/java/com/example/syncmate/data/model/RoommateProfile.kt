package com.example.syncmate.data.model

data class RoommateProfile(
    val id: String,
    val name: String,
    val university: String,
    val avatarUrl: String? = null,
    val compatibilityPercentage: Int,
    val matchReason: String,
    val lifestyleRhythm: String, // e.g. "Early Riser • Clean & Quiet"
    val maxBudgetMonthly: Double,
    val preferredLocation: String,
    val isSaved: Boolean = false,
    val bio: String = ""
)
