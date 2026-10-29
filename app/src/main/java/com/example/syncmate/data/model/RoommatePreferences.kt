package com.example.syncmate.data.model

data class RoommatePreferences(
    val sleepSchedule: String = "Early Bird", // Early Bird / Night Owl / Flexible
    val cleanlinessRating: Int = 4, // 1 to 5
    val guestPolicy: String = "Weekends Only", // Never / Weekends Only / Anytime
    val noiseTolerance: String = "Moderate", // Quiet Study / Moderate / Loud Music OK
    val studyHabits: String = "In Room", // In Room / Library / Common Areas
    val dietaryPreference: String = "Vegetarian", // Vegetarian / Non-Vegetarian / Vegan / Flexible
    val smokingOption: Boolean = false, // false = Non-Smoking Only, true = Tolerant
    val petPreference: String = "Love Pets", // Love Pets / No Pets / Pet Owner
    val sharingStyle: String = "Open to Sharing Essentials", // Strict Mine-is-Mine / Open to Sharing Essentials
    val maxBudgetINR: Double = 15000.0 // Maximum Monthly Rent in ₹
)
