package com.example.syncmate.data.model

data class RoommateRequest(
    val requestId: String = "",
    val senderId: String = "",
    val senderName: String = "",
    val senderUniversity: String = "",
    val receiverId: String = "",
    val receiverName: String = "",
    val compatibilityScore: Int = 0,
    val introMessage: String = "",
    val status: String = "PENDING", // PENDING, ACCEPTED, DECLINED
    val timestamp: Long = System.currentTimeMillis()
)
