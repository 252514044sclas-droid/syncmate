package com.example.syncmate.viewmodel

import androidx.lifecycle.ViewModel
import com.example.syncmate.data.local.AppPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class SettingsUiState(
    val darkModeEnabled: Boolean = true,
    val notificationsEnabled: Boolean = true,
    val rememberDevice: Boolean = true,
    val feedbackSubmitted: Boolean = false
)

class SettingsViewModel(
    private val appPreferences: AppPreferences
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        SettingsUiState(
            darkModeEnabled = appPreferences.darkModeEnabled,
            notificationsEnabled = appPreferences.notificationsEnabled,
            rememberDevice = appPreferences.isRememberDevice
        )
    )
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    fun toggleDarkMode(enabled: Boolean) {
        appPreferences.darkModeEnabled = enabled
        _uiState.value = _uiState.value.copy(darkModeEnabled = enabled)
    }

    fun toggleNotifications(enabled: Boolean) {
        appPreferences.notificationsEnabled = enabled
        _uiState.value = _uiState.value.copy(notificationsEnabled = enabled)
    }

    fun toggleRememberDevice(enabled: Boolean) {
        appPreferences.isRememberDevice = enabled
        _uiState.value = _uiState.value.copy(rememberDevice = enabled)
    }

    fun submitFeedback(feedbackText: String, rating: Int) {
        _uiState.value = _uiState.value.copy(feedbackSubmitted = true)
    }

    fun clearFeedbackStatus() {
        _uiState.value = _uiState.value.copy(feedbackSubmitted = false)
    }
}
