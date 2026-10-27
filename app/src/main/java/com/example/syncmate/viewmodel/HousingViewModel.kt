package com.example.syncmate.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.syncmate.data.model.HousingListing
import com.example.syncmate.data.repository.HousingRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class HousingUiState(
    val isLoading: Boolean = false,
    val listings: List<HousingListing> = emptyList(),
    val selectedListing: HousingListing? = null,
    val savedListings: List<HousingListing> = emptyList(),
    val compareListings: List<HousingListing> = emptyList(),
    val maxPriceFilter: Double = 2000.0,
    val selectedRoomType: String = "All",
    val isFurnishedOnly: Boolean = false,
    val reportSubmitted: Boolean = false,
    val error: String? = null
)

class HousingViewModel(
    private val housingRepository: HousingRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HousingUiState(isLoading = true))
    val uiState: StateFlow<HousingUiState> = _uiState.asStateFlow()

    init {
        loadAllListings()
    }

    private fun loadAllListings() {
        viewModelScope.launch {
            housingRepository.getListings().collect { listings ->
                val saved = listings.filter { it.isSaved }
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    listings = listings,
                    savedListings = saved
                )
            }
        }
    }

    fun loadListingById(id: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            val listing = housingRepository.getListingById(id)
            _uiState.value = _uiState.value.copy(
                isLoading = false,
                selectedListing = listing
            )
        }
    }

    fun toggleSaveListing(id: String) {
        viewModelScope.launch {
            housingRepository.toggleSaveListing(id)
        }
    }

    fun toggleCompareListing(listing: HousingListing) {
        val currentCompare = _uiState.value.compareListings.toMutableList()
        if (currentCompare.any { it.id == listing.id }) {
            currentCompare.removeAll { it.id == listing.id }
        } else if (currentCompare.size < 3) {
            currentCompare.add(listing)
        }
        _uiState.value = _uiState.value.copy(compareListings = currentCompare)
    }

    fun updateFilters(maxPrice: Double, roomType: String, furnished: Boolean) {
        _uiState.value = _uiState.value.copy(
            maxPriceFilter = maxPrice,
            selectedRoomType = roomType,
            isFurnishedOnly = furnished
        )
    }

    fun reportListing(id: String, reason: String, comments: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            val result = housingRepository.reportListing(id, reason, comments)
            result.onSuccess {
                _uiState.value = _uiState.value.copy(isLoading = false, reportSubmitted = true)
            }.onFailure { ex ->
                _uiState.value = _uiState.value.copy(isLoading = false, error = ex.message)
            }
        }
    }

    fun createListing(
        title: String,
        address: String,
        price: Double,
        bedrooms: Int,
        bathrooms: Double,
        roomType: String,
        description: String,
        ownerName: String,
        ownerEmail: String
    ) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            val newListing = HousingListing(
                id = "lst_" + System.currentTimeMillis(),
                title = title,
                address = address,
                priceMonthly = price,
                bedrooms = bedrooms,
                bathrooms = bathrooms,
                roomType = roomType,
                duration = "Summer 2025",
                description = description,
                ownerName = ownerName,
                ownerEmail = ownerEmail
            )
            housingRepository.createListing(newListing)
            _uiState.value = _uiState.value.copy(isLoading = false)
        }
    }
}
