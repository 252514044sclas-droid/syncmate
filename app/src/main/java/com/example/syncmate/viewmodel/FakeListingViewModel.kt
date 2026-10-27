package com.example.syncmate.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.syncmate.data.model.FakeListingAnalysis
import com.example.syncmate.data.repository.FakeListingRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class FakeListingUiState(
    val isLoading: Boolean = false,
    val analysis: FakeListingAnalysis? = null,
    val error: String? = null
)

class FakeListingViewModel(
    private val fakeListingRepository: FakeListingRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(FakeListingUiState())
    val uiState: StateFlow<FakeListingUiState> = _uiState.asStateFlow()

    fun analyzeListing(description: String, priceStr: String, imageUrls: List<String>) {
        val price = priceStr.toDoubleOrNull() ?: 800.0

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            val result = fakeListingRepository.analyzeListingForFraud(description, price, imageUrls)
            result.onSuccess { data ->
                _uiState.value = _uiState.value.copy(isLoading = false, analysis = data)
            }.onFailure { ex ->
                _uiState.value = _uiState.value.copy(isLoading = false, error = ex.message ?: "Analysis failed")
            }
        }
    }

    fun clearAnalysis() {
        _uiState.value = FakeListingUiState()
    }
}
