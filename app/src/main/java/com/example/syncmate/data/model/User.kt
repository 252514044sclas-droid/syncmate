package com.example.syncmate.data.model

data class User(
    val id: String = "",
    val name: String = "",
    val email: String = "",
    val university: String = "",
    val isVerified: Boolean = false,
    val profilePictureUrl: String? = null,
    val bio: String = "",
    val preferredLeaseTerm: String = "Summer 2025",
    val maxBudget: Double = 15000.0,
    val preferredLocation: String = "Near Campus / Metro",
    val hasCompletedQuiz: Boolean = false,
    val roommatePreferences: RoommatePreferences? = null
)
