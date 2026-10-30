package com.example.syncmate.viewmodel

import androidx.lifecycle.ViewModel
import com.example.syncmate.data.local.AppPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class SettingsUiState(
    val darkModeEnabled: Boolean = true,
    val notificationsEnabled: Boolean = true,
    val priceDropAlertsEnabled: Boolean = true,
    val chatAlertsEnabled: Boolean = true,
    val rememberDevice: Boolean = true,
    val cacheCleared: Boolean = false,
    val feedbackSubmitted: Boolean = false
)

class SettingsViewModel(
    private val appPreferences: AppPreferences
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        SettingsUiState(
            darkModeEnabled = appPreferences.darkModeEnabled,
            notificationsEnabled = appPreferences.notificationsEnabled,
            priceDropAlertsEnabled = appPreferences.priceDropAlertsEnabled,
            chatAlertsEnabled = appPreferences.chatAlertsEnabled,
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

    fun togglePriceDropAlerts(enabled: Boolean) {
        appPreferences.priceDropAlertsEnabled = enabled
        _uiState.value = _uiState.value.copy(priceDropAlertsEnabled = enabled)
    }

    fun toggleChatAlerts(enabled: Boolean) {
        appPreferences.chatAlertsEnabled = enabled
        _uiState.value = _uiState.value.copy(chatAlertsEnabled = enabled)
    }

    fun toggleRememberDevice(enabled: Boolean) {
        appPreferences.isRememberDevice = enabled
        _uiState.value = _uiState.value.copy(rememberDevice = enabled)
    }

    fun clearCache() {
        _uiState.value = _uiState.value.copy(cacheCleared = true)
    }

    fun submitFeedback(feedbackText: String, rating: Int) {
        _uiState.value = _uiState.value.copy(feedbackSubmitted = true)
    }

    fun clearFeedbackStatus() {
        _uiState.value = _uiState.value.copy(feedbackSubmitted = false)
    }
}
