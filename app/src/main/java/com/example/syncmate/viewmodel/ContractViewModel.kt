package com.example.syncmate.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.syncmate.data.model.ContractSummary
import com.example.syncmate.data.repository.ContractRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class ContractUiState(
    val isLoading: Boolean = false,
    val selectedFileName: String? = null,
    val summary: ContractSummary? = null,
    val error: String? = null
)

class ContractViewModel(
    private val contractRepository: ContractRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ContractUiState())
    val uiState: StateFlow<ContractUiState> = _uiState.asStateFlow()

    fun analyzeContract(fileName: String, fileBytes: ByteArray? = null) {
        _uiState.value = _uiState.value.copy(selectedFileName = fileName, isLoading = true, error = null)
        viewModelScope.launch {
            val result = contractRepository.analyzeContractDocument(fileName, fileBytes)
            result.onSuccess { summary ->
                _uiState.value = _uiState.value.copy(isLoading = false, summary = summary)
            }.onFailure { ex ->
                _uiState.value = _uiState.value.copy(isLoading = false, error = ex.message ?: "Contract analysis failed")
            }
        }
    }

    fun resetState() {
        _uiState.value = ContractUiState()
    }
}
