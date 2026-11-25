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
import kotlinx.coroutines.flow.firstOrNull
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

    private var usersListener: com.google.firebase.firestore.ListenerRegistration? = null

    override fun onCleared() {
        super.onCleared()
        usersListener?.remove()
    }

    private fun loadDashboardData() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)

            // Reactively stream real-time roommate matches from Firestore based on current user session
            viewModelScope.launch {
                authRepository.currentUserFlow.collect { user ->
                    val firstName = user?.name?.substringBefore(" ") ?: "Student"
                    _uiState.value = _uiState.value.copy(userName = firstName)

                    usersListener?.remove()
                    val currentUserId = user?.id ?: ""
                    usersListener = com.example.syncmate.data.firestore.SyncMateFirestore.attachUsersSnapshotListener(currentUserId) { users ->
                        val liveRoommates = users
                            .distinctBy { it.id }
                            .distinctBy { "${it.name.trim().lowercase()}_${it.university.trim().lowercase()}" }
                            .map { u ->
                            val rhythm = listOfNotNull(
                                u.roommatePreferences?.sleepSchedule,
                                if (u.roommatePreferences?.smokingOption == false) "Non-Smoker" else null,
                                u.roommatePreferences?.noiseTolerance
                            ).joinToString(" • ")

                            RoommateProfile(
                                id = u.id,
                                name = u.name,
                                university = u.university,
                                compatibilityPercentage = 92,
                                matchReason = "Matches your ${u.roommatePreferences?.sleepSchedule ?: "lifestyle"} & budget preferences",
                                lifestyleRhythm = rhythm.ifEmpty { "Verified Student • Clean & Quiet" },
                                maxBudgetMonthly = u.roommatePreferences?.maxBudgetINR ?: u.maxBudget,
                                preferredLocation = u.preferredLocation,
                                bio = u.bio
                            )
                        }
                        _uiState.value = _uiState.value.copy(roommateMatches = liveRoommates)
                    }
                }
            }

            housingRepository.getListings().collect { listings ->
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    recommendedListings = listings
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
