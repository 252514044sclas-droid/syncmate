package com.example.syncmate.data.repository

import com.example.syncmate.data.model.HousingListing
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

interface HousingRepository {
    fun getListings(): Flow<List<HousingListing>>
    suspend fun getListingById(id: String): HousingListing?
    suspend fun toggleSaveListing(id: String)
    suspend fun createListing(listing: HousingListing): Result<HousingListing>
    suspend fun reportListing(id: String, reason: String, comments: String): Result<Boolean>
}

class MockHousingRepositoryImpl : HousingRepository {
    private val mockListings = MutableStateFlow(
        listOf(
            HousingListing(
                id = "lst_1",
                title = "Modern 1BHK Sublet Near Engineering Campus",
                address = "Adyar, Chennai",
                priceMonthly = 14500.0,
                bedrooms = 1,
                bathrooms = 1.0,
                roomType = "Private Room",
                isFurnished = true,
                utilitiesIncluded = true,
                duration = "May 15 - Aug 15, 2025",
                description = "Spacious private bedroom with attached bath in a modern apartment near IIT Madras / Anna University. Fully furnished with high-speed WiFi included. 5 min walk to campus library.",
                ownerName = "Sarah Chen",
                ownerEmail = "sarah.c@university.edu",
                ownerPhone = "+91 98401 23456",
                isVerifiedStudent = true,
                distanceToCampus = "0.4 km",
                riskScore = 4,
                predictedFairRent = 14000.0,
                isSaved = true
            ),
            HousingListing(
                id = "lst_2",
                title = "Luxury Studio at Student Hub",
                address = "Koramangala, Bengaluru",
                priceMonthly = 22000.0,
                bedrooms = 1,
                bathrooms = 1.0,
                roomType = "Entire Studio",
                isFurnished = true,
                utilitiesIncluded = false,
                duration = "June 1 - Aug 31, 2025",
                description = "Full studio apartment available for summer sublease near tech park and colleges. Top floor view, rooftop terrace access, washing machine, study desk, and power backup included.",
                ownerName = "Marcus Vance",
                ownerEmail = "mvance@student.edu",
                ownerPhone = "+91 98801 34567",
                isVerifiedStudent = true,
                distanceToCampus = "0.8 km",
                riskScore = 8,
                predictedFairRent = 21500.0,
                isSaved = false
            ),
            HousingListing(
                id = "lst_3",
                title = "Cozy Private Room in 3BHK Flat",
                address = "Kothrud, Pune",
                priceMonthly = 9500.0,
                bedrooms = 3,
                bathrooms = 2.0,
                roomType = "Private Room",
                isFurnished = true,
                utilitiesIncluded = false,
                duration = "May 1 - Aug 1, 2025",
                description = "Charming room in a peaceful student neighborhood near MIT / COEP. Large balcony, two-wheeler parking, bus and metro station directly accessible.",
                ownerName = "Emily Rodriguez",
                ownerEmail = "emily.r@university.edu",
                ownerPhone = "+91 98220 45678",
                isVerifiedStudent = true,
                distanceToCampus = "1.2 km",
                riskScore = 12,
                predictedFairRent = 9200.0,
                isSaved = true
            ),
            HousingListing(
                id = "lst_4",
                title = "Spacious 2BHK Sublet - Gated Community",
                address = "Hitec City, Hyderabad",
                priceMonthly = 18500.0,
                bedrooms = 2,
                bathrooms = 2.0,
                roomType = "Entire Apartment",
                isFurnished = true,
                utilitiesIncluded = true,
                duration = "May 20 - Aug 20, 2025",
                description = "Looking for a responsible student to sublet 2BHK unit near IIIT Hyderabad. Great amenities including gym, study lounges, security, and power backup.",
                ownerName = "David Kim",
                ownerEmail = "dkim@university.edu",
                ownerPhone = "+91 99490 56789",
                isVerifiedStudent = true,
                distanceToCampus = "0.6 km",
                riskScore = 6,
                predictedFairRent = 18000.0,
                isSaved = false
            ),
            HousingListing(
                id = "lst_5",
                title = "Student Friendly Room Near North Campus",
                address = "North Campus, Delhi",
                priceMonthly = 11500.0,
                bedrooms = 2,
                bathrooms = 1.0,
                roomType = "Private Room",
                isFurnished = true,
                utilitiesIncluded = true,
                duration = "May 1 - July 31, 2025",
                description = "Well-ventilated room 5 minutes walk from Vishwavidyalaya Metro and Delhi University colleges. High-speed broadband and clean drinking water setup included.",
                ownerName = "Aman Sharma",
                ownerEmail = "aman.s@university.edu",
                ownerPhone = "+91 98110 67890",
                isVerifiedStudent = true,
                distanceToCampus = "0.5 km",
                riskScore = 5,
                predictedFairRent = 11000.0,
                isSaved = false
            )
        )
    )

    override fun getListings(): Flow<List<HousingListing>> = mockListings.asStateFlow()

    override suspend fun getListingById(id: String): HousingListing? {
        return mockListings.value.find { it.id == id }
    }

    override suspend fun toggleSaveListing(id: String) {
        mockListings.value = mockListings.value.map { listing ->
            if (listing.id == id) listing.copy(isSaved = !listing.isSaved) else listing
        }
    }

    override suspend fun createListing(listing: HousingListing): Result<HousingListing> {
        delay(1000)
        val newListings = mockListings.value.toMutableList()
        newListings.add(0, listing)
        mockListings.value = newListings
        return Result.success(listing)
    }

    override suspend fun reportListing(id: String, reason: String, comments: String): Result<Boolean> {
        delay(800)
        return Result.success(true)
    }
}
