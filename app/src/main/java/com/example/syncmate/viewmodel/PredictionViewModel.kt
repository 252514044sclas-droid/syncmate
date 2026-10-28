package com.example.syncmate.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.syncmate.data.model.RentPredictionRequest
import com.example.syncmate.data.model.RentPredictionResult
import com.example.syncmate.data.repository.PredictionRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class PredictionUiState(
    val isLoading: Boolean = false,
    val result: RentPredictionResult? = null,
    val error: String? = null
)

class PredictionViewModel(
    private val predictionRepository: PredictionRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(PredictionUiState())
    val uiState: StateFlow<PredictionUiState> = _uiState.asStateFlow()

    fun predictFairRent(
        location: String,
        sqFtStr: String,
        bedroomsStr: String,
        bathroomsStr: String,
        roomType: String,
        leaseDurationStr: String,
        season: String,
        facilities: List<String>
    ) {
        val sqFt = sqFtStr.toIntOrNull() ?: 650
        val bedrooms = bedroomsStr.toIntOrNull() ?: 1
        val bathrooms = bathroomsStr.toDoubleOrNull() ?: 1.0
        val leaseDuration = leaseDurationStr.toIntOrNull() ?: 3

        val request = RentPredictionRequest(
            location = location.ifEmpty { "Adyar, Chennai" },
            squareFeet = sqFt,
            bedrooms = bedrooms,
            bathrooms = bathrooms,
            roomType = roomType,
            facilities = facilities,
            leaseDurationMonths = leaseDuration,
            season = season
        )

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            val res = predictionRepository.predictFairRentAndUtilities(request)
            res.onSuccess { data ->
                _uiState.value = _uiState.value.copy(isLoading = false, result = data)
            }.onFailure { ex ->
                _uiState.value = _uiState.value.copy(isLoading = false, error = ex.message ?: "Prediction failed")
            }
        }
    }

    fun clearResult() {
        _uiState.value = PredictionUiState()
    }
}
