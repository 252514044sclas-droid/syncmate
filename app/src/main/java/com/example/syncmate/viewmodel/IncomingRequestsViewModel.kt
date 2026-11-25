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
    val incomingRequests: List<IncomingRequestUiModel> = emptyList(),
    val sentRequests: List<IncomingRequestUiModel> = emptyList(),
    val selectedTab: String = "Incoming", // "Incoming" or "Sent"
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
            authRepository.currentUserFlow.filterNotNull().collectLatest { currentUser ->
                val currentUserId = currentUser.id

                listenerRegistration?.remove()
                listenerRegistration = SyncMateFirestore.attachRequestsSnapshotListener(currentUserId) { incoming, sent ->
                    val incomingModels = incoming.map { req ->
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

                    val sentModels = sent.map { req ->
                        val receiver = SyncMateFirestore.getUser(req.receiverId)
                        val prefs = receiver?.roommatePreferences
                        IncomingRequestUiModel(
                            request = req,
                            senderUser = receiver,
                            sleepSchedule = prefs?.sleepSchedule ?: "Early Bird",
                            cleanlinessRating = prefs?.cleanlinessRating ?: 4,
                            budgetINR = prefs?.maxBudgetINR ?: (receiver?.maxBudget ?: 15000.0),
                            isVerified = receiver?.isVerified ?: true
                        )
                    }

                    val activeTab = _uiState.value.selectedTab
                    val activeList = if (activeTab == "Sent") sentModels else incomingModels

                    _uiState.value = _uiState.value.copy(
                        incomingRequests = incomingModels,
                        sentRequests = sentModels,
                        requests = activeList,
                        pendingCount = incomingModels.count { it.request.status == "PENDING" },
                        isLoading = false
                    )
                }
            }
        }
    }

    fun setSelectedTab(tab: String) {
        val activeList = if (tab == "Sent") _uiState.value.sentRequests else _uiState.value.incomingRequests
        _uiState.value = _uiState.value.copy(
            selectedTab = tab,
            requests = activeList
        )
    }

    fun acceptRequest(requestId: String) {
        viewModelScope.launch {
            val existing = _uiState.value.incomingRequests.firstOrNull { it.request.requestId == requestId }?.request
            SyncMateFirestore.updateRequestStatus(requestId, "ACCEPTED", existing)
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
