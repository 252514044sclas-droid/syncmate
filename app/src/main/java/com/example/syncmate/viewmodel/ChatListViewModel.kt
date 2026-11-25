package com.example.syncmate.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.syncmate.data.firestore.ListenerRegistration
import com.example.syncmate.data.firestore.SyncMateFirestore
import com.example.syncmate.data.model.ChatRoom
import com.example.syncmate.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class ChatListItemUiModel(
    val chatRoom: ChatRoom,
    val otherUserId: String,
    val otherUserName: String,
    val otherUserUniversity: String = "Student Peer",
    val isVerified: Boolean = true,
    val relativeTime: String = "Just now"
)

data class ChatListUiState(
    val chats: List<ChatListItemUiModel> = emptyList(),
    val currentUserId: String = "",
    val isLoading: Boolean = true,
    val errorMessage: String? = null
)

class ChatListViewModel(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ChatListUiState())
    val uiState: StateFlow<ChatListUiState> = _uiState.asStateFlow()

    private var chatsListener: ListenerRegistration? = null

    init {
        startListening()
    }

    private fun startListening() {
        viewModelScope.launch {
            authRepository.currentUserFlow.filterNotNull().collectLatest { currentUser ->
                val currentUserId = currentUser.id
                _uiState.value = _uiState.value.copy(currentUserId = currentUserId)

                chatsListener?.remove()
                chatsListener = SyncMateFirestore.attachChatsSnapshotListener(currentUserId) { chatRooms ->
                    val sortedRooms = chatRooms.sortedByDescending { it.lastMessageTimestamp }

                    val mappedList = sortedRooms.map { room ->
                        val otherUserId = room.participants.firstOrNull { it != currentUserId } ?: ""
                        val cachedUser = SyncMateFirestore.getUser(otherUserId)
                        val otherUserName = room.participantNames[otherUserId]
                            ?: cachedUser?.name
                            ?: "Roommate Peer"

                        val otherUserUniversity = cachedUser?.university ?: "Verified Student"

                        ChatListItemUiModel(
                            chatRoom = room,
                            otherUserId = otherUserId,
                            otherUserName = otherUserName,
                            otherUserUniversity = otherUserUniversity,
                            isVerified = cachedUser?.isVerified ?: true,
                            relativeTime = formatRelativeTime(room.lastMessageTimestamp)
                        )
                    }

                    _uiState.value = _uiState.value.copy(
                        chats = mappedList,
                        isLoading = false
                    )
                }
            }
        }
    }

    private fun formatRelativeTime(timestamp: Long): String {
        if (timestamp <= 0L) return ""
        val diff = System.currentTimeMillis() - timestamp
        val minutes = diff / (1000 * 60)
        val hours = minutes / 60
        val days = hours / 24

        return when {
            diff < 0 -> "Just now"
            minutes < 2 -> "Just now"
            minutes < 60 -> "${minutes}m ago"
            hours < 24 -> "${hours}h ago"
            days == 1L -> "Yesterday"
            days < 7 -> "${days}d ago"
            else -> SimpleDateFormat("MMM d", Locale.getDefault()).format(Date(timestamp))
        }
    }

    override fun onCleared() {
        super.onCleared()
        chatsListener?.remove()
    }
}
