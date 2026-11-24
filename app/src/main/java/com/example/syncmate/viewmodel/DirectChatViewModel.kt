package com.example.syncmate.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.syncmate.data.firestore.ListenerRegistration
import com.example.syncmate.data.firestore.SyncMateFirestore
import com.example.syncmate.data.model.DirectMessage
import com.example.syncmate.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

data class DirectChatUiState(
    val messages: List<DirectMessage> = emptyList(),
    val chatId: String = "",
    val otherUserId: String = "",
    val otherUserName: String = "",
    val otherUserUniversity: String = "Verified Student",
    val isVerified: Boolean = true,
    val currentUserId: String = "",
    val currentUserName: String = "",
    val isLoading: Boolean = true,
    val errorMessage: String? = null
)

class DirectChatViewModel(
    val chatId: String,
    val otherUserId: String,
    val otherUserName: String,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        DirectChatUiState(
            chatId = chatId,
            otherUserId = otherUserId,
            otherUserName = otherUserName
        )
    )
    val uiState: StateFlow<DirectChatUiState> = _uiState.asStateFlow()

    private var messagesListener: ListenerRegistration? = null

    init {
        initializeChat()
    }

    private fun initializeChat() {
        viewModelScope.launch {
            val currentUser = authRepository.currentUserFlow.firstOrNull()
            val currentUserId = currentUser?.id ?: "usr_dev_101"
            val currentUserName = currentUser?.name ?: "Student"

            val cachedOtherUser = SyncMateFirestore.getUser(otherUserId)
            val otherUniversity = cachedOtherUser?.university ?: "Verified Student"
            val isVerified = cachedOtherUser?.isVerified ?: true

            // Ensure parent chat document exists at chats/{chatId}
            SyncMateFirestore.getOrCreateChatRoom(
                userAId = currentUserId,
                userAName = currentUserName,
                userBId = otherUserId,
                userBName = otherUserName
            )

            _uiState.value = _uiState.value.copy(
                currentUserId = currentUserId,
                currentUserName = currentUserName,
                otherUserUniversity = otherUniversity,
                isVerified = isVerified
            )

            // Attach real-time snapshot listener on chats/{chatId}/messages ordered by timestamp ascending
            messagesListener = SyncMateFirestore.attachMessagesSnapshotListener(chatId) { messages ->
                val sorted = messages.sortedBy { it.timestamp }
                _uiState.value = _uiState.value.copy(
                    messages = sorted,
                    isLoading = false
                )
            }
        }
    }

    fun sendMessage(text: String) {
        val trimmed = text.trim()
        if (trimmed.isEmpty()) return

        val state = _uiState.value
        viewModelScope.launch {
            try {
                SyncMateFirestore.sendDirectMessage(
                    chatId = chatId,
                    senderId = state.currentUserId,
                    senderName = state.currentUserName,
                    text = trimmed
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(errorMessage = "Failed to send message: ${e.message}")
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        messagesListener?.remove()
    }
}
