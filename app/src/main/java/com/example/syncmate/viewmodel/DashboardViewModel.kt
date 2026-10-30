package com.example.syncmate.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.syncmate.data.model.HousingListing
import com.example.syncmate.data.model.RoommateProfile
import com.example.syncmate.data.repository.AuthRepository
import com.example.syncmate.data.repository.HousingRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class DashboardUiState(
    val isLoading: Boolean = false,
    val userName: String = "Alex",
    val unreadNotificationCount: Int = 3,
    val unreadMessageCount: Int = 2,
    val searchQuery: String = "",
    val recommendedListings: List<HousingListing> = emptyList(),
    val roommateMatches: List<RoommateProfile> = emptyList(),
    val error: String? = null
)

class DashboardViewModel(
    private val authRepository: AuthRepository,
    private val housingRepository: HousingRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(DashboardUiState(isLoading = true))
    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()

    init {
        loadDashboardData()
    }

    private fun loadDashboardData() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)

            val sampleRoommates = listOf(
                RoommateProfile(
                    id = "rm_1",
                    name = "Maya Lin",
                    university = "Anna University '26",
                    compatibilityPercentage = 96,
                    matchReason = "Matches your quiet study hours & budget",
                    lifestyleRhythm = "Early Riser • Non-Smoker",
                    maxBudgetMonthly = 12000.0,
                    preferredLocation = "Adyar / Guindy",
                    bio = "CS junior looking for a clean, peaceful sublet room for summer internship."
                ),
                RoommateProfile(
                    id = "rm_2",
                    name = "Jordan Patel",
                    university = "IIT Madras '25",
                    compatibilityPercentage = 91,
                    matchReason = "Similar lease term & campus quadrant",
                    lifestyleRhythm = "Night Owl • Quiet & Organized",
                    maxBudgetMonthly = 16500.0,
                    preferredLocation = "Koramangala, Bengaluru",
                    bio = "Grad student in ECE. Friendly, respectful, and loves cooking on weekends."
                ),
                RoommateProfile(
                    id = "rm_3",
                    name = "Chloe Bennett",
                    university = "Delhi University '25",
                    compatibilityPercentage = 88,
                    matchReason = "Shared campus commute & budget preference",
                    lifestyleRhythm = "Flexible • Social & Clean",
                    maxBudgetMonthly = 14000.0,
                    preferredLocation = "North Campus, Delhi",
                    bio = "Commerce major looking for a sublet roommate near North Campus."
                )
            )

            // Reactively collect current user updates (such as name changes)
            viewModelScope.launch {
                authRepository.currentUserFlow.collect { user ->
                    val firstName = user?.name?.substringBefore(" ") ?: "Student"
                    _uiState.value = _uiState.value.copy(userName = firstName)
                }
            }

            housingRepository.getListings().collect { listings ->
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    recommendedListings = listings,
                    roommateMatches = sampleRoommates
                )
            }
        }
    }

    fun onSearchQueryChanged(query: String) {
        _uiState.value = _uiState.value.copy(searchQuery = query)
    }

    fun toggleSaveListing(id: String) {
        viewModelScope.launch {
            housingRepository.toggleSaveListing(id)
        }
    }

    fun toggleFavoriteRoommate(id: String) {
        val updatedRoommates = _uiState.value.roommateMatches.map { roommate ->
            if (roommate.id == id) roommate.copy(isSaved = !roommate.isSaved) else roommate
        }
        _uiState.value = _uiState.value.copy(roommateMatches = updatedRoommates)
    }
}
