package com.example.syncmate.data.remote.dto

import com.google.gson.annotations.SerializedName

data class HousingListingDto(
    @SerializedName("id") val id: String,
    @SerializedName("title") val title: String,
    @SerializedName("address") val address: String,
    @SerializedName("price_monthly") val priceMonthly: Double,
    @SerializedName("bedrooms") val bedrooms: Int,
    @SerializedName("bathrooms") val bathrooms: Double,
    @SerializedName("room_type") val roomType: String,
    @SerializedName("is_furnished") val isFurnished: Boolean = true,
    @SerializedName("utilities_included") val utilitiesIncluded: Boolean = false,
    @SerializedName("duration") val duration: String,
    @SerializedName("description") val description: String,
    @SerializedName("image_urls") val imageUrls: List<String> = emptyList(),
    @SerializedName("owner_name") val ownerName: String,
    @SerializedName("owner_email") val ownerEmail: String,
    @SerializedName("owner_phone") val ownerPhone: String = "",
    @SerializedName("is_verified_student") val isVerifiedStudent: Boolean = true,
    @SerializedName("distance_to_campus") val distanceToCampus: String = "0.5 km",
    @SerializedName("risk_score") val riskScore: Int = 5,
    @SerializedName("predicted_fair_rent") val predictedFairRent: Double = priceMonthly,
    @SerializedName("is_saved") val isSaved: Boolean = false,
    @SerializedName("amenities") val amenities: List<String> = emptyList()
)

data class CreateListingRequestDto(
    @SerializedName("title") val title: String,
    @SerializedName("address") val address: String,
    @SerializedName("price_monthly") val priceMonthly: Double,
    @SerializedName("bedrooms") val bedrooms: Int,
    @SerializedName("bathrooms") val bathrooms: Double,
    @SerializedName("room_type") val roomType: String,
    @SerializedName("description") val description: String
)

data class ReportListingRequestDto(
    @SerializedName("listing_id") val listingId: String,
    @SerializedName("reason") val reason: String,
    @SerializedName("comments") val comments: String
)

data class SaveListingRequestDto(
    @SerializedName("listing_id") val listingId: String
)
