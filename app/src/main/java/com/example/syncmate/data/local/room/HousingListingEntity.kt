package com.example.syncmate.data.local.room

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.syncmate.data.model.HousingListing

@Entity(tableName = "housing_listings")
data class HousingListingEntity(
    @PrimaryKey val id: String,
    val title: String,
    val address: String,
    val priceMonthly: Double,
    val bedrooms: Int,
    val bathrooms: Double,
    val roomType: String,
    val isFurnished: Boolean,
    val utilitiesIncluded: Boolean,
    val duration: String,
    val description: String,
    val imageUrls: List<String>,
    val ownerName: String,
    val ownerEmail: String,
    val ownerPhone: String,
    val isVerifiedStudent: Boolean,
    val distanceToCampus: String,
    val riskScore: Int,
    val predictedFairRent: Double,
    val isSaved: Boolean,
    val amenities: List<String>
) {
    fun toDomain() = HousingListing(
        id = id,
        title = title,
        address = address,
        priceMonthly = priceMonthly,
        bedrooms = bedrooms,
        bathrooms = bathrooms,
        roomType = roomType,
        isFurnished = isFurnished,
        utilitiesIncluded = utilitiesIncluded,
        duration = duration,
        description = description,
        imageUrls = imageUrls,
        ownerName = ownerName,
        ownerEmail = ownerEmail,
        ownerPhone = ownerPhone,
        isVerifiedStudent = isVerifiedStudent,
        distanceToCampus = distanceToCampus,
        riskScore = riskScore,
        predictedFairRent = predictedFairRent,
        isSaved = isSaved,
        amenities = amenities
    )
}

fun HousingListing.toEntity() = HousingListingEntity(
    id = id,
    title = title,
    address = address,
    priceMonthly = priceMonthly,
    bedrooms = bedrooms,
    bathrooms = bathrooms,
    roomType = roomType,
    isFurnished = isFurnished,
    utilitiesIncluded = utilitiesIncluded,
    duration = duration,
    description = description,
    imageUrls = imageUrls,
    ownerName = ownerName,
    ownerEmail = ownerEmail,
    ownerPhone = ownerPhone,
    isVerifiedStudent = isVerifiedStudent,
    distanceToCampus = distanceToCampus,
    riskScore = riskScore,
    predictedFairRent = predictedFairRent,
    isSaved = isSaved,
    amenities = amenities
)