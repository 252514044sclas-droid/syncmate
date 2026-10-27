package com.example.syncmate.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.syncmate.data.model.HousingListing
import com.example.syncmate.data.repository.HousingRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class HomeUiState(
    val isLoading: Boolean = false,
    val featuredListings: List<HousingListing> = emptyList(),
    val searchQuery: String = "",
    val activeCategory: String = "All",
    val error: String? = null
)

class HomeViewModel(
    private val housingRepository: HousingRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState(isLoading = true))
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        loadListings()
    }

    fun loadListings() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            housingRepository.getListings().collect { listings ->
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    featuredListings = listings
                )
            }
        }
    }

    fun onSearchQueryChanged(query: String) {
        _uiState.value = _uiState.value.copy(searchQuery = query)
    }

    fun onCategorySelected(category: String) {
        _uiState.value = _uiState.value.copy(activeCategory = category)
    }

    fun toggleSaveListing(id: String) {
        viewModelScope.launch {
            housingRepository.toggleSaveListing(id)
        }
    }
}
