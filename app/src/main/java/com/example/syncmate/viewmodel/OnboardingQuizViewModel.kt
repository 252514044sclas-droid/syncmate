package com.example.syncmate.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.syncmate.data.model.RoommatePreferences
import com.example.syncmate.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class QuizUiState(
    val currentQuestionIndex: Int = 0, // 0 to 9
    val preferences: RoommatePreferences = RoommatePreferences(),
    val isLoading: Boolean = false,
    val isCompleted: Boolean = false,
    val error: String? = null
)

class OnboardingQuizViewModel(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(QuizUiState())
    val uiState: StateFlow<QuizUiState> = _uiState.asStateFlow()

    fun nextQuestion() {
        val currentIndex = _uiState.value.currentQuestionIndex
        if (currentIndex < 9) {
            _uiState.value = _uiState.value.copy(currentQuestionIndex = currentIndex + 1)
        } else {
            submitPreferences()
        }
    }

    fun previousQuestion() {
        val currentIndex = _uiState.value.currentQuestionIndex
        if (currentIndex > 0) {
            _uiState.value = _uiState.value.copy(currentQuestionIndex = currentIndex - 1)
        }
    }

    fun updateSleepSchedule(schedule: String) {
        _uiState.value = _uiState.value.copy(
            preferences = _uiState.value.preferences.copy(sleepSchedule = schedule)
        )
    }

    fun updateCleanliness(rating: Int) {
        _uiState.value = _uiState.value.copy(
            preferences = _uiState.value.preferences.copy(cleanlinessRating = rating)
        )
    }

    fun updateGuestPolicy(policy: String) {
        _uiState.value = _uiState.value.copy(
            preferences = _uiState.value.preferences.copy(guestPolicy = policy)
        )
    }

    fun updateNoiseTolerance(noise: String) {
        _uiState.value = _uiState.value.copy(
            preferences = _uiState.value.preferences.copy(noiseTolerance = noise)
        )
    }

    fun updateStudyHabits(habits: String) {
        _uiState.value = _uiState.value.copy(
            preferences = _uiState.value.preferences.copy(studyHabits = habits)
        )
    }

    fun updateDietaryPreference(diet: String) {
        _uiState.value = _uiState.value.copy(
            preferences = _uiState.value.preferences.copy(dietaryPreference = diet)
        )
    }

    fun updateSmokingOption(tolerant: Boolean) {
        _uiState.value = _uiState.value.copy(
            preferences = _uiState.value.preferences.copy(smokingOption = tolerant)
        )
    }

    fun updatePetPreference(pets: String) {
        _uiState.value = _uiState.value.copy(
            preferences = _uiState.value.preferences.copy(petPreference = pets)
        )
    }

    fun updateSharingStyle(sharing: String) {
        _uiState.value = _uiState.value.copy(
            preferences = _uiState.value.preferences.copy(sharingStyle = sharing)
        )
    }

    fun updateMaxBudget(budget: Double) {
        _uiState.value = _uiState.value.copy(
            preferences = _uiState.value.preferences.copy(maxBudgetINR = budget)
        )
    }

    fun submitPreferences() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            val currentPrefs = _uiState.value.preferences
            _uiState.value = _uiState.value.copy(isLoading = false, isCompleted = true)
        }
    }
}
