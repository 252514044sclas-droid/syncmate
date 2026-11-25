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
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.launch

data class RoommateRequestsUiState(
    val incomingRequests: List<RoommateRequest> = emptyList(),
    val sentRequests: List<RoommateRequest> = emptyList(),
    val selectedTab: Int = 0, // 0: Incoming, 1: Sent
    val pendingIncomingCount: Int = 0,
    val isLoading: Boolean = true,
    val actionMessage: String? = null,
    val errorMessage: String? = null
)

class RoommateRequestsViewModel(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(RoommateRequestsUiState())
    val uiState: StateFlow<RoommateRequestsUiState> = _uiState.asStateFlow()

    private var requestsListener: ListenerRegistration? = null
    private var currentUserId: String = ""

    init {
        startRealtimeListener()
    }

    private fun startRealtimeListener() {
        viewModelScope.launch {
            authRepository.currentUserFlow.filterNotNull().collectLatest { user ->
                currentUserId = user.id

                // Attach real-time snapshot listener on the "roommate_requests" collection
                // Simple queries only - list is sorted in Kotlin memory by timestamp descending
                // to completely avoid Firestore unbuilt composite index failures.
                requestsListener?.remove()
                requestsListener = SyncMateFirestore.attachRequestsSnapshotListener(currentUserId) { incoming, sent ->
                    val distinctIncoming = incoming
                        .sortedByDescending { it.timestamp }
                        .distinctBy { it.senderId }
                        .distinctBy { "${it.senderName.trim().lowercase()}_${it.senderUniversity.trim().lowercase()}" }

                    val distinctSent = sent
                        .sortedByDescending { it.timestamp }
                        .distinctBy { it.receiverId }
                        .distinctBy { "${it.receiverName.trim().lowercase()}_${it.receiverUniversity.trim().lowercase()}" }

                    val pendingCount = distinctIncoming.count { it.status == "PENDING" }

                    _uiState.value = _uiState.value.copy(
                        incomingRequests = distinctIncoming,
                        sentRequests = distinctSent,
                        pendingIncomingCount = pendingCount,
                        isLoading = false
                    )
                }
            }
        }
    }

    fun selectTab(index: Int) {
        _uiState.value = _uiState.value.copy(selectedTab = index)
    }

    fun acceptRequest(requestId: String) {
        viewModelScope.launch {
            try {
                val existing = _uiState.value.incomingRequests.firstOrNull { it.requestId == requestId }
                SyncMateFirestore.updateRequestStatus(requestId, "ACCEPTED", existing)
                _uiState.value = _uiState.value.copy(
                    actionMessage = "Request accepted! Direct 1-on-1 chat unlocked.",
                    errorMessage = null
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    errorMessage = "Failed to accept request: ${e.message}"
                )
            }
        }
    }

    fun declineRequest(requestId: String) {
        viewModelScope.launch {
            try {
                SyncMateFirestore.updateRequestStatus(requestId, "DECLINED")
                _uiState.value = _uiState.value.copy(
                    actionMessage = "Request declined.",
                    errorMessage = null
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    errorMessage = "Failed to decline request: ${e.message}"
                )
            }
        }
    }

    fun cancelRequest(requestId: String) {
        viewModelScope.launch {
            try {
                SyncMateFirestore.cancelRoommateRequest(requestId)
                _uiState.value = _uiState.value.copy(
                    actionMessage = "Sent request cancelled.",
                    errorMessage = null
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    errorMessage = "Failed to cancel request: ${e.message}"
                )
            }
        }
    }

    fun clearActionMessage() {
        _uiState.value = _uiState.value.copy(actionMessage = null)
    }

    fun clearErrorMessage() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }

    override fun onCleared() {
        super.onCleared()
        requestsListener?.remove()
    }
}
