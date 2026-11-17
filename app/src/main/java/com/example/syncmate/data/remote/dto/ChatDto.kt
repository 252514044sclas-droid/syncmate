package com.example.syncmate.data.remote.dto

import com.google.gson.annotations.SerializedName

data class ChatHistoryItemDto(
    @SerializedName("role") val role: String,
    @SerializedName("content") val content: String
)

data class ChatRequestDto(
    @SerializedName("message") val message: String,
    @SerializedName("history") val history: List<ChatHistoryItemDto> = emptyList(),
    @SerializedName("api_key") val apiKey: String? = null
)

data class ChatResponseDto(
    @SerializedName("reply") val reply: String
)
