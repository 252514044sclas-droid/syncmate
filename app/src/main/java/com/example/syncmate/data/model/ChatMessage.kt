package com.example.syncmate.data.model

data class ChatMessage(
    val id: String = System.currentTimeMillis().toString(),
    val text: String,
    val isMe: Boolean,
    val timestamp: String
)
