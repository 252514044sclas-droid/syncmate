package com.example.syncmate.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.syncmate.data.firestore.ListenerRegistration
import com.example.syncmate.data.firestore.SyncMateFirestore
import com.example.syncmate.data.model.RoommateRequest
import com.example.syncmate.data.model.User
import com.example.syncmate.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

data class IncomingRequestUiModel(
    val request: RoommateRequest,
    val senderUser: User? = null,
    val sleepSchedule: String = "Early Bird",
    val cleanlinessRating: Int = 4,
    val budgetINR: Double = 15000.0,
    val isVerified: Boolean = true
)

data class IncomingRequestsUiState(
    val requests: List<IncomingRequestUiModel> = emptyList(),
    val isLoading: Boolean = false,
    val pendingCount: Int = 0,
    val error: String? = null,
    val actionMessage: String? = null
)

class IncomingRequestsViewModel(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(IncomingRequestsUiState(isLoading = true))
    val uiState: StateFlow<IncomingRequestsUiState> = _uiState.asStateFlow()

    private var listenerRegistration: ListenerRegistration? = null

    init {
        startListening()
    }

    private fun startListening() {
        viewModelScope.launch {
            val currentUser = authRepository.currentUserFlow.firstOrNull()
            val currentUserId = currentUser?.id ?: "usr_dev_101"

            listenerRegistration = SyncMateFirestore.attachIncomingPendingRequestsListener(currentUserId) { requests ->
                val uiModels = requests.map { req ->
                    val sender = SyncMateFirestore.getUser(req.senderId)
                    val prefs = sender?.roommatePreferences
                    IncomingRequestUiModel(
                        request = req,
                        senderUser = sender,
                        sleepSchedule = prefs?.sleepSchedule ?: "Early Bird",
                        cleanlinessRating = prefs?.cleanlinessRating ?: 4,
                        budgetINR = prefs?.maxBudgetINR ?: (sender?.maxBudget ?: 15000.0),
                        isVerified = sender?.isVerified ?: true
                    )
                }

                _uiState.value = _uiState.value.copy(
                    requests = uiModels,
                    pendingCount = uiModels.size,
                    isLoading = false
                )
            }
        }
    }

    fun acceptRequest(requestId: String) {
        viewModelScope.launch {
            SyncMateFirestore.updateRequestStatus(requestId, "ACCEPTED")
            _uiState.value = _uiState.value.copy(
                actionMessage = "Request Accepted! Direct 1-on-1 chat unlocked."
            )
        }
    }

    fun declineRequest(requestId: String) {
        viewModelScope.launch {
            SyncMateFirestore.updateRequestStatus(requestId, "DECLINED")
            _uiState.value = _uiState.value.copy(
                actionMessage = "Request declined."
            )
        }
    }

    fun clearActionMessage() {
        _uiState.value = _uiState.value.copy(actionMessage = null)
    }

    override fun onCleared() {
        super.onCleared()
        listenerRegistration?.remove()
    }
}
