package com.example.syncmate.data.model

data class RoommatePreferences(
    val sleepSchedule: String = "", // Early Bird / Night Owl / Flexible
    val cleanlinessRating: Int = 3, // 1 to 5
    val guestPolicy: String = "", // Never / Weekends Only / Anytime
    val noiseTolerance: String = "", // Quiet Study / Moderate / Loud Music OK
    val studyHabits: String = "", // In Room / Library / Common Areas
    val dietaryPreference: String = "", // Vegetarian / Non-Vegetarian / Vegan / Flexible
    val smokingOption: Boolean? = null, // null = unselected, false = Non-Smoking Only, true = Tolerant
    val petPreference: String = "", // Love Pets / No Pets / Pet Owner
    val sharingStyle: String = "", // Strict Mine-is-Mine / Open to Sharing Essentials
    val maxBudgetINR: Double = 0.0 // Maximum Monthly Rent in ₹
)
