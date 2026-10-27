package com.example.syncmate.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.syncmate.data.model.User
import com.example.syncmate.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class AuthUiState(
    val isLoading: Boolean = false,
    val user: User? = null,
    val isLoggedIn: Boolean = false,
    val error: String? = null,
    val rememberDevice: Boolean = true,
    val passwordResetSent: Boolean = false,
    val emailVerificationSuccess: Boolean = false
)

class AuthViewModel(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            authRepository.currentUserFlow.collect { user ->
                _uiState.value = _uiState.value.copy(
                    user = user,
                    isLoggedIn = user != null
                )
            }
        }
    }

    fun login(email: String, pass: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            val result = authRepository.login(email, pass)
            result.onSuccess { user ->
                _uiState.value = _uiState.value.copy(isLoading = false, user = user, isLoggedIn = true)
            }.onFailure { ex ->
                _uiState.value = _uiState.value.copy(isLoading = false, error = ex.message ?: "Login failed")
            }
        }
    }

    fun signUp(name: String, email: String, pass: String, university: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            val result = authRepository.signUp(name, email, pass, university)
            result.onSuccess { user ->
                _uiState.value = _uiState.value.copy(isLoading = false, user = user, isLoggedIn = true)
            }.onFailure { ex ->
                _uiState.value = _uiState.value.copy(isLoading = false, error = ex.message ?: "Registration failed")
            }
        }
    }

    fun forgotPassword(email: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null, passwordResetSent = false)
            val result = authRepository.forgotPassword(email)
            result.onSuccess {
                _uiState.value = _uiState.value.copy(isLoading = false, passwordResetSent = true)
            }.onFailure { ex ->
                _uiState.value = _uiState.value.copy(isLoading = false, error = ex.message ?: "Password reset failed")
            }
        }
    }

    fun verifyEmail(email: String, code: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            val result = authRepository.verifyEmail(email, code)
            result.onSuccess { success ->
                _uiState.value = _uiState.value.copy(isLoading = false, emailVerificationSuccess = success)
            }.onFailure { ex ->
                _uiState.value = _uiState.value.copy(isLoading = false, error = ex.message ?: "Verification failed")
            }
        }
    }

    fun toggleRememberDevice(remember: Boolean) {
        _uiState.value = _uiState.value.copy(rememberDevice = remember)
    }

    fun signOut() {
        viewModelScope.launch {
            authRepository.signOut()
            _uiState.value = AuthUiState()
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}
