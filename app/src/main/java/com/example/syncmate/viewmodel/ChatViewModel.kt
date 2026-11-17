package com.example.syncmate.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.syncmate.data.model.ChatMessage
import com.example.syncmate.data.repository.ChatRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class ChatUiState(
    val messages: List<ChatMessage> = emptyList(),
    val isTyping: Boolean = false,
    val error: String? = null
)

class ChatViewModel(
    private val chatRepository: ChatRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ChatUiState())
    val uiState: StateFlow<ChatUiState> = _uiState.asStateFlow()

    init {
        loadHistory()
    }

    private fun loadHistory() {
        val saved = chatRepository.getSavedMessages()
        _uiState.value = _uiState.value.copy(messages = saved)
    }

    private fun getCurrentTimestamp(): String {
        return SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Date())
    }

    fun sendMessage(userText: String) {
        val trimmed = userText.trim()
        if (trimmed.isEmpty()) return

        val userMessage = ChatMessage(
            id = System.currentTimeMillis().toString(),
            text = trimmed,
            isMe = true,
            timestamp = getCurrentTimestamp()
        )

        // Capture previous messages for conversation history/context
        val currentHistory = _uiState.value.messages
        val updatedWithUser = currentHistory + userMessage

        _uiState.value = _uiState.value.copy(
            messages = updatedWithUser,
            isTyping = true,
            error = null
        )
        chatRepository.saveMessages(updatedWithUser)

        viewModelScope.launch {
            val result = chatRepository.sendMessage(trimmed, currentHistory)

            result.onSuccess { aiReply ->
                val aiMessage = ChatMessage(
                    id = (System.currentTimeMillis() + 1).toString(),
                    text = aiReply,
                    isMe = false,
                    timestamp = getCurrentTimestamp()
                )
                val updatedWithAi = _uiState.value.messages + aiMessage
                _uiState.value = _uiState.value.copy(
                    messages = updatedWithAi,
                    isTyping = false,
                    error = null
                )
                chatRepository.saveMessages(updatedWithAi)
            }.onFailure { exception ->
                val fallbackErrorMessage = ChatMessage(
                    id = (System.currentTimeMillis() + 1).toString(),
                    text = "I'm having trouble connecting to the network right now. Please ensure your backend server is reachable and try again.",
                    isMe = false,
                    timestamp = getCurrentTimestamp()
                )
                val updatedWithError = _uiState.value.messages + fallbackErrorMessage
                _uiState.value = _uiState.value.copy(
                    messages = updatedWithError,
                    isTyping = false,
                    error = exception.message ?: "Failed to get AI response"
                )
                chatRepository.saveMessages(updatedWithError)
            }
        }
    }

    fun clearChat() {
        chatRepository.clearMessages()
        _uiState.value = ChatUiState(messages = emptyList(), isTyping = false, error = null)
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}
