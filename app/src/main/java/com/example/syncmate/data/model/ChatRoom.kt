package com.example.syncmate.data.model

data class ChatRoom(
    val chatId: String = "",
    val participants: List<String> = emptyList(),
    val participantNames: Map<String, String> = emptyMap(),
    val lastMessage: String = "",
    val lastMessageTimestamp: Long = 0L,
    val compatibilityScore: Int = 0
)
