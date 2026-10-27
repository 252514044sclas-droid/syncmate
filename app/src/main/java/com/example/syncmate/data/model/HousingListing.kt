package com.example.syncmate.data.model

data class HousingListing(
    val id: String,
    val title: String,
    val address: String,
    val priceMonthly: Double,
    val bedrooms: Int,
    val bathrooms: Double,
    val roomType: String, // e.g. "Private Room", "Entire Apartment", "Shared Room"
    val isFurnished: Boolean = true,
    val utilitiesIncluded: Boolean = false,
    val duration: String, // e.g., "May 2025 - Aug 2025"
    val description: String,
    val imageUrls: List<String> = emptyList(),
    val ownerName: String,
    val ownerEmail: String,
    val ownerPhone: String = "",
    val isVerifiedStudent: Boolean = true,
    val distanceToCampus: String = "0.5 miles",
    val riskScore: Int = 5, // 0-100% risk score
    val predictedFairRent: Double = priceMonthly,
    val isSaved: Boolean = false,
    val amenities: List<String> = listOf("WiFi", "In-unit Laundry", "AC", "Gym", "Parking")
)
