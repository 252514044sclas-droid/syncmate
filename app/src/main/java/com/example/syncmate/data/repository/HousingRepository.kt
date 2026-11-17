package com.example.syncmate.data.repository

import com.example.syncmate.data.model.HousingListing
import com.example.syncmate.data.remote.ApiService
import com.example.syncmate.data.remote.dto.CreateListingRequestDto
import com.example.syncmate.data.remote.dto.HousingListingDto
import com.example.syncmate.data.remote.dto.ReportListingRequestDto
import com.example.syncmate.data.remote.dto.SaveListingRequestDto
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

class MockHousingRepositoryImpl(
    private val apiService: ApiService? = null
) : HousingRepository {
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
                duration = "Summer Sublet (May 15 - Aug 15, 2025)",
                description = "Spacious private bedroom with attached bath in a modern apartment near IIT Madras / Anna University. Fully furnished with high-speed WiFi included.",
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
                duration = "Fall Semester Sublet (Sep 1 - Dec 20, 2025)",
                description = "Full studio apartment available for summer sublease near tech park and colleges. Top floor view, rooftop terrace access, washing machine, study desk, and power backup.",
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
                duration = "Immediate Move-in (Available Now)",
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
                duration = "Full Academic Year Sublet (11 Months)",
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
                duration = "Short-Term Internship Sublet (June 1 - July 31, 2025)",
                description = "Well-ventilated room 5 minutes walk from Vishwavidyalaya Metro and Delhi University colleges. High-speed broadband and clean drinking water setup included.",
                ownerName = "Aman Sharma",
                ownerEmail = "aman.s@university.edu",
                ownerPhone = "+91 98110 67890",
                isVerifiedStudent = true,
                distanceToCampus = "0.5 km",
                riskScore = 5,
                predictedFairRent = 11000.0,
                isSaved = false
            ),
            HousingListing(
                id = "lst_6",
                title = "Lakeview Studio Flat Near IIT Powai",
                address = "Powai, Mumbai",
                priceMonthly = 24000.0,
                bedrooms = 1,
                bathrooms = 1.0,
                roomType = "Entire Studio",
                isFurnished = true,
                utilitiesIncluded = true,
                duration = "Immediate Move-in (Available Now)",
                description = "Scenic studio room with Powai Lake view near IIT Bombay. Modern kitchenette, high-speed WiFi, AC, 24/7 security guard, and elevator access.",
                ownerName = "Rohan Mehta",
                ownerEmail = "rohan.m@iitb.ac.in",
                ownerPhone = "+91 98200 11223",
                isVerifiedStudent = true,
                distanceToCampus = "0.3 km",
                riskScore = 3,
                predictedFairRent = 23500.0,
                isSaved = false
            ),
            HousingListing(
                id = "lst_7",
                title = "Vibrant 2BHK Sublet Near Symbiosis",
                address = "Viman Nagar, Pune",
                priceMonthly = 13500.0,
                bedrooms = 2,
                bathrooms = 2.0,
                roomType = "Private Room",
                isFurnished = true,
                utilitiesIncluded = false,
                duration = "Spring Semester Sublet (Jan 5 - Apr 30, 2026)",
                description = "Bright room with attached balcony near Symbiosis campus and Phoenix Mall. Full kitchen, high-speed internet, washing machine, and maid service included.",
                ownerName = "Priya Deshmukh",
                ownerEmail = "priya.d@symbiosis.edu",
                ownerPhone = "+91 98900 33445",
                isVerifiedStudent = true,
                distanceToCampus = "0.7 km",
                riskScore = 4,
                predictedFairRent = 13000.0,
                isSaved = false
            ),
            HousingListing(
                id = "lst_8",
                title = "Modern Shared Room Near OMR IT Corridor",
                address = "OMR, Chennai",
                priceMonthly = 7800.0,
                bedrooms = 3,
                bathrooms = 3.0,
                roomType = "Shared Room",
                isFurnished = true,
                utilitiesIncluded = true,
                duration = "Summer Sublet (May 15 - Aug 15, 2025)",
                description = "Affordable shared student twin-sharing room near SSN / Satyabama and tech parks. Includes AC, high-speed WiFi, food mess option, and gym.",
                ownerName = "Karthik Raja",
                ownerEmail = "karthik.r@university.edu",
                ownerPhone = "+91 97900 55667",
                isVerifiedStudent = true,
                distanceToCampus = "1.0 km",
                riskScore = 5,
                predictedFairRent = 7500.0,
                isSaved = false
            ),
            HousingListing(
                id = "lst_9",
                title = "Chic Studio Near Indiranagar Metro",
                address = "Indiranagar, Bengaluru",
                priceMonthly = 21000.0,
                bedrooms = 1,
                bathrooms = 1.0,
                roomType = "Entire Studio",
                isFurnished = true,
                utilitiesIncluded = false,
                duration = "Immediate Move-in (Available Now)",
                description = "Chic studio flat within 3 minutes walk to Indiranagar Metro Station. Great cafes, supermarkets, fast internet, study table, and power backup.",
                ownerName = "Ananya Iyer",
                ownerEmail = "ananya.i@student.edu",
                ownerPhone = "+91 98450 77889",
                isVerifiedStudent = true,
                distanceToCampus = "0.9 km",
                riskScore = 6,
                predictedFairRent = 20500.0,
                isSaved = false
            ),
            HousingListing(
                id = "lst_10",
                title = "Peaceful Sublet Room Near Salt Lake Sector V",
                address = "Salt Lake, Kolkata",
                priceMonthly = 8500.0,
                bedrooms = 2,
                bathrooms = 1.5,
                roomType = "Private Room",
                isFurnished = true,
                utilitiesIncluded = true,
                duration = "Short-Term Internship Sublet (June 1 - July 31, 2025)",
                description = "Comfortable private room in Salt Lake near IT sector and educational hubs. Quiet study balcony, Broadband WiFi, aqua guard filter, and cook available.",
                ownerName = "Subhash Bannerjee",
                ownerEmail = "subhash.b@university.edu",
                ownerPhone = "+91 98300 99001",
                isVerifiedStudent = true,
                distanceToCampus = "1.1 km",
                riskScore = 4,
                predictedFairRent = 8200.0,
                isSaved = false
            ),
            HousingListing(
                id = "lst_11",
                title = "Gachibowli Student Suite Near IIIT & ISB",
                address = "Gachibowli, Hyderabad",
                priceMonthly = 16000.0,
                bedrooms = 1,
                bathrooms = 1.0,
                roomType = "Private Room",
                isFurnished = true,
                utilitiesIncluded = true,
                duration = "Fall Semester Sublet (Sep 1 - Dec 20, 2025)",
                description = "Modern room in luxury student gated community near Gachibowli Stadium and campuses. AC, power backup, clubhouse, gym, and swimming pool access.",
                ownerName = "Vikram Reddy",
                ownerEmail = "vikram.r@iiit.ac.in",
                ownerPhone = "+91 98490 22334",
                isVerifiedStudent = true,
                distanceToCampus = "0.5 km",
                riskScore = 5,
                predictedFairRent = 15500.0,
                isSaved = false
            ),
            HousingListing(
                id = "lst_12",
                title = "College Avenue Apartment Near IIM Ahmedabad",
                address = "Navrangpura, Ahmedabad",
                priceMonthly = 10500.0,
                bedrooms = 2,
                bathrooms = 2.0,
                roomType = "Private Room",
                isFurnished = true,
                utilitiesIncluded = false,
                duration = "Immediate Move-in (Available Now)",
                description = "Spacious room 5 minutes walk from IIM Ahmedabad and Gujarat University. AC, high-speed WiFi, private balcony, refrigerator, and two-wheeler parking.",
                ownerName = "Nihar Patel",
                ownerEmail = "nihar.p@iima.ac.in",
                ownerPhone = "+91 98250 44556",
                isVerifiedStudent = true,
                distanceToCampus = "0.4 km",
                riskScore = 4,
                predictedFairRent = 10000.0,
                isSaved = false
            ),
            HousingListing(
                id = "lst_13",
                title = "Cozy Private Room Near CEG Anna University",
                address = "Guindy, Chennai",
                priceMonthly = 12000.0,
                bedrooms = 1,
                bathrooms = 1.0,
                roomType = "Private Room",
                isFurnished = true,
                utilitiesIncluded = true,
                duration = "Summer Sublet (May 15 - Aug 15, 2025)",
                description = "Quiet student room right next to Guindy Metro and Anna University CEG campus. Fully furnished, high-speed Wi-Fi, study table, and washing machine.",
                ownerName = "Deepika Ramesh",
                ownerEmail = "deepika.r@annauniv.edu",
                ownerPhone = "+91 98402 33445",
                isVerifiedStudent = true,
                distanceToCampus = "0.3 km",
                riskScore = 3,
                predictedFairRent = 11800.0,
                isSaved = false
            ),
            HousingListing(
                id = "lst_14",
                title = "Spacious 2BHK Flat Near Loyola College",
                address = "Nungambakkam, Chennai",
                priceMonthly = 19000.0,
                bedrooms = 2,
                bathrooms = 2.0,
                roomType = "Entire Apartment",
                isFurnished = true,
                utilitiesIncluded = false,
                duration = "Immediate Move-in (Available Now)",
                description = "Central Nungambakkam apartment within walking distance to Loyola College and WCC. Gated security, power backup, open kitchen, and balcony.",
                ownerName = "Siddharth Varma",
                ownerEmail = "siddharth.v@student.edu",
                ownerPhone = "+91 98410 55667",
                isVerifiedStudent = true,
                distanceToCampus = "0.5 km",
                riskScore = 5,
                predictedFairRent = 18500.0,
                isSaved = false
            ),
            HousingListing(
                id = "lst_15",
                title = "Student PG Room Near T. Nagar Shopping Hub",
                address = "T. Nagar, Chennai",
                priceMonthly = 8900.0,
                bedrooms = 1,
                bathrooms = 1.0,
                roomType = "Private Room",
                isFurnished = true,
                utilitiesIncluded = true,
                duration = "Short-Term Internship Sublet (June 1 - July 31, 2025)",
                description = "Affordable student accommodation in T. Nagar near bus terminus, restaurants, and colleges. Includes Wi-Fi, 3 meals option, and laundry.",
                ownerName = "Magesh Kumar",
                ownerEmail = "magesh.k@university.edu",
                ownerPhone = "+91 98420 77889",
                isVerifiedStudent = true,
                distanceToCampus = "1.1 km",
                riskScore = 4,
                predictedFairRent = 8500.0,
                isSaved = false
            ),
            HousingListing(
                id = "lst_16",
                title = "Modern Studio Flat Near Velachery MRTS",
                address = "Velachery, Chennai",
                priceMonthly = 16500.0,
                bedrooms = 1,
                bathrooms = 1.0,
                roomType = "Entire Studio",
                isFurnished = true,
                utilitiesIncluded = true,
                duration = "Fall Semester Sublet (Sep 1 - Dec 20, 2025)",
                description = "Independent studio close to Velachery Phoenix Mall and IIT Madras checkpost. AC, fast internet, attached kitchenette, and covered bike parking.",
                ownerName = "Divya Sundar",
                ownerEmail = "divya.s@university.edu",
                ownerPhone = "+91 98430 99001",
                isVerifiedStudent = true,
                distanceToCampus = "0.8 km",
                riskScore = 5,
                predictedFairRent = 16000.0,
                isSaved = false
            ),
            HousingListing(
                id = "lst_17",
                title = "Beachside Student Flat Near Thiruvanmiyur",
                address = "Thiruvanmiyur, Chennai",
                priceMonthly = 21000.0,
                bedrooms = 2,
                bathrooms = 2.0,
                roomType = "Entire Apartment",
                isFurnished = true,
                utilitiesIncluded = false,
                duration = "Spring Semester Sublet (Jan 5 - Apr 30, 2026)",
                description = "Breezy apartment 5 minutes from Thiruvanmiyur beach and Tidal Park. Ideal for students doing research at IIT Research Park or tech internships.",
                ownerName = "Arjun Natarajan",
                ownerEmail = "arjun.n@iitm.ac.in",
                ownerPhone = "+91 98440 12345",
                isVerifiedStudent = true,
                distanceToCampus = "0.9 km",
                riskScore = 6,
                predictedFairRent = 20500.0,
                isSaved = false
            ),
            HousingListing(
                id = "lst_18",
                title = "Budget Student Dorm Room Near MCC",
                address = "Tambaram, Chennai",
                priceMonthly = 6500.0,
                bedrooms = 2,
                bathrooms = 1.0,
                roomType = "Shared Room",
                isFurnished = true,
                utilitiesIncluded = true,
                duration = "Full Academic Year Sublet (11 Months)",
                description = "Very close to Madras Christian College (MCC) Tambaram campus. Safe student accommodation with mess food, study hall, and 24/7 water supply.",
                ownerName = "Sundar Raman",
                ownerEmail = "sundar.r@mcc.edu.in",
                ownerPhone = "+91 98450 67890",
                isVerifiedStudent = true,
                distanceToCampus = "0.4 km",
                riskScore = 3,
                predictedFairRent = 6200.0,
                isSaved = false
            )
        )
    )

    override fun getListings(): Flow<List<HousingListing>> = mockListings.asStateFlow()

    override suspend fun getListingById(id: String): HousingListing? {
        if (apiService != null) {
            try {
                val res = apiService.getListingDetail(id)
                if (res.isSuccessful && res.body()?.data != null) {
                    return res.body()!!.data!!.toDomain()
                }
            } catch (_: Exception) {}
        }
        return mockListings.value.find { it.id == id }
    }

    override suspend fun toggleSaveListing(id: String) {
        if (apiService != null) {
            try {
                apiService.toggleSaveListing(SaveListingRequestDto(id))
            } catch (_: Exception) {}
        }
        mockListings.value = mockListings.value.map { listing ->
            if (listing.id == id) listing.copy(isSaved = !listing.isSaved) else listing
        }
    }

    override suspend fun createListing(listing: HousingListing): Result<HousingListing> {
        if (apiService != null) {
            try {
                val req = CreateListingRequestDto(
                    title = listing.title,
                    address = listing.address,
                    priceMonthly = listing.priceMonthly,
                    bedrooms = listing.bedrooms,
                    bathrooms = listing.bathrooms,
                    roomType = listing.roomType,
                    description = listing.description
                )
                val res = apiService.createListing(req)
                if (res.isSuccessful && res.body()?.data != null) {
                    val created = res.body()!!.data!!.toDomain()
                    val newListings = mockListings.value.toMutableList()
                    newListings.add(0, created)
                    mockListings.value = newListings
                    return Result.success(created)
                }
            } catch (_: Exception) {}
        }

        delay(800)
        val newListings = mockListings.value.toMutableList()
        newListings.add(0, listing)
        mockListings.value = newListings
        return Result.success(listing)
    }

    override suspend fun reportListing(id: String, reason: String, comments: String): Result<Boolean> {
        if (apiService != null) {
            try {
                val res = apiService.reportListing(ReportListingRequestDto(id, reason, comments))
                if (res.isSuccessful) return Result.success(true)
            } catch (_: Exception) {}
        }
        delay(600)
        return Result.success(true)
    }

    private fun HousingListingDto.toDomain() = HousingListing(
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
