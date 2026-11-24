package com.example.syncmate.data.remote.dto

import com.google.gson.annotations.SerializedName

data class SendDirectMessageDto(
    @SerializedName("chat_id") val chatId: String,
    @SerializedName("message_id") val messageId: String,
    @SerializedName("sender_id") val senderId: String,
    @SerializedName("sender_name") val senderName: String,
    @SerializedName("text") val text: String,
    @SerializedName("timestamp") val timestamp: Long
)

data class DirectMessageDto(
    @SerializedName("message_id") val messageId: String,
    @SerializedName("chat_id") val chatId: String,
    @SerializedName("sender_id") val senderId: String,
    @SerializedName("sender_name") val senderName: String,
    @SerializedName("text") val text: String,
    @SerializedName("timestamp") val timestamp: Long
)

data class ChatRoomDto(
    @SerializedName("chat_id") val chatId: String,
    @SerializedName("participants") val participants: List<String>,
    @SerializedName("participant_names") val participantNames: Map<String, String>,
    @SerializedName("last_message") val lastMessage: String,
    @SerializedName("last_message_timestamp") val lastMessageTimestamp: Long,
    @SerializedName("compatibility_score") val compatibilityScore: Int
)

data class CreateChatRoomDto(
    @SerializedName("chat_id") val chatId: String,
    @SerializedName("participants") val participants: List<String>,
    @SerializedName("participant_names") val participantNames: Map<String, String>,
    @SerializedName("last_message") val lastMessage: String = "",
    @SerializedName("last_message_timestamp") val lastMessageTimestamp: Long = 0L,
    @SerializedName("compatibility_score") val compatibilityScore: Int = 0
)
