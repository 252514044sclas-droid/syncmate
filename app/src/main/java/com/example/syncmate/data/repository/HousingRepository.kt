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
                title = "Modern 1BR Sublet Near Engineering Quad",
                address = "402 College Ave, Campus Town",
                priceMonthly = 850.0,
                bedrooms = 1,
                bathrooms = 1.0,
                roomType = "Private Room",
                isFurnished = true,
                utilitiesIncluded = true,
                duration = "May 15 - Aug 15, 2025",
                description = "Spacious private bedroom with private bath in a modern 4BR apartment. Fully furnished with high-speed internet included. 5 min walk to campus library.",
                ownerName = "Sarah Chen",
                ownerEmail = "sarah.c@university.edu",
                ownerPhone = "(555) 234-5678",
                isVerifiedStudent = true,
                distanceToCampus = "0.3 miles",
                riskScore = 4,
                predictedFairRent = 875.0,
                isSaved = true
            ),
            HousingListing(
                id = "lst_2",
                title = "Luxury Studio at Campus Edge Towers",
                address = "110 N 1st Street",
                priceMonthly = 1250.0,
                bedrooms = 1,
                bathrooms = 1.0,
                roomType = "Entire Studio",
                isFurnished = true,
                utilitiesIncluded = false,
                duration = "June 1 - Aug 31, 2025",
                description = "Full studio apartment available for summer sublease. Top floor view, rooftop pool access, in-unit laundry, dishwasher, and fitness center included.",
                ownerName = "Marcus Vance",
                ownerEmail = "mvance@student.edu",
                isVerifiedStudent = true,
                distanceToCampus = "0.6 miles",
                riskScore = 8,
                predictedFairRent = 1200.0,
                isSaved = false
            ),
            HousingListing(
                id = "lst_3",
                title = "Cozy Bedroom in 3BR House with Backyard",
                address = "714 Green Street",
                priceMonthly = 620.0,
                bedrooms = 3,
                bathrooms = 1.5,
                roomType = "Private Room",
                isFurnished = false,
                utilitiesIncluded = false,
                duration = "May 1 - Aug 1, 2025",
                description = "Charming room in a peaceful student neighborhood. Large yard, free driveway parking, bus line directly outside.",
                ownerName = "Emily Rodriguez",
                ownerEmail = "emily.r@university.edu",
                isVerifiedStudent = true,
                distanceToCampus = "1.2 miles",
                riskScore = 12,
                predictedFairRent = 650.0,
                isSaved = true
            ),
            HousingListing(
                id = "lst_4",
                title = "Spacious 2BR Sublet - Pool & Gym Access",
                address = "901 S Wright St",
                priceMonthly = 990.0,
                bedrooms = 2,
                bathrooms = 2.0,
                roomType = "Entire Apartment",
                isFurnished = true,
                utilitiesIncluded = true,
                duration = "May 20 - Aug 20, 2025",
                description = "Looking for a responsible student to sublet 2BR unit. Great amenities including pool, study lounges, and gym.",
                ownerName = "David Kim",
                ownerEmail = "dkim@university.edu",
                isVerifiedStudent = true,
                distanceToCampus = "0.4 miles",
                riskScore = 6,
                predictedFairRent = 1050.0,
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

    override suspend fun reportListing(
        id: String,
        reason: String,
        comments: String
    ): Result<Boolean> {
        delay(800)
        return Result.success(true)
    }
}
