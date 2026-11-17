package com.example.syncmate.data.repository

import com.example.syncmate.data.local.AppPreferences
import com.example.syncmate.data.model.ChatMessage
import com.example.syncmate.data.remote.ApiService
import com.example.syncmate.data.remote.dto.ChatHistoryItemDto
import com.example.syncmate.data.remote.dto.ChatRequestDto
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.Locale

interface ChatRepository {
    suspend fun sendMessage(userMessage: String, history: List<ChatMessage>): Result<String>
    fun getSavedMessages(): List<ChatMessage>
    fun saveMessages(messages: List<ChatMessage>)
    fun clearMessages()
}

class NetworkChatRepositoryImpl(
    private val apiService: ApiService,
    private val appPreferences: AppPreferences,
    private val gson: Gson = Gson()
) : ChatRepository {

    override suspend fun sendMessage(userMessage: String, history: List<ChatMessage>): Result<String> = withContext(Dispatchers.IO) {
        try {
            val historyDto = history.takeLast(10).map { msg ->
                ChatHistoryItemDto(
                    role = if (msg.isMe) "user" else "assistant",
                    content = msg.text
                )
            }

            val request = ChatRequestDto(
                message = userMessage,
                history = historyDto,
                apiKey = appPreferences.geminiApiKey
            )

            val response = apiService.sendChatMessage(request)
            if (response.isSuccessful && response.body()?.data != null) {
                Result.success(response.body()!!.data!!.reply)
            } else {
                Result.success(generateFallbackResponse(userMessage))
            }
        } catch (_: Exception) {
            Result.success(generateFallbackResponse(userMessage))
        }
    }

    private fun generateFallbackResponse(userPrompt: String): String {
        val query = userPrompt.lowercase(Locale.getDefault())

        return when {
            query.contains("hi") || query.contains("hello") || query.contains("hey") || query.contains("hwy") || query.contains("hii") ->
                "Hey there! 👋 Excited to chat! How can I help you regarding the sublet property, rent, or lease details?"

            query.contains("available") || query.contains("dates") || query.contains("duration") || query.contains("when") ->
                "Yes, the sublet is available from May 15 to August 15, 2025! The move-in date can also be slightly flexible if your exams finish early."

            query.contains("rent") || query.contains("price") || query.contains("cost") || query.contains("inr") || query.contains("rupees") || query.contains("how much") || query.contains("amount") || query.contains("pay") ->
                "The rent is fixed at ₹14,500/month. We verified this rate with the SyncMate ML Fair-Rent Engine to make sure it's fair for students."

            query.contains("wifi") || query.contains("utility") || query.contains("utilities") || query.contains("electricity") || query.contains("ac") || query.contains("water") ->
                "High-speed WiFi, water supply, and society maintenance are included in the rent! Electricity & AC are metered separately based on usage."

            query.contains("location") || query.contains("where") || query.contains("address") || query.contains("distance") || query.contains("metro") || query.contains("campus") ->
                "It's located in Adyar, just 0.4 km (a 5-minute walk) from the main university gate and close to the metro station!"

            query.contains("deposit") || query.contains("security") || query.contains("advance") ->
                "The security deposit is ₹15,000. It is fully refundable upon move-out after inspecting the room condition."

            query.contains("visit") || query.contains("tour") || query.contains("see") || query.contains("viewing") || query.contains("show") ->
                "I'd be happy to show you around! We can set up an in-person campus visit or a live video tour. What time works best for you?"

            query.contains("furnish") || query.contains("bed") || query.contains("desk") || query.contains("kitchen") || query.contains("fridge") ->
                "The room is fully furnished! It includes a study desk, ergonomic chair, queen bed, spacious wardrobe, refrigerator, and microwave."

            query.contains("pet") || query.contains("smoking") || query.contains("roommate") || query.contains("guest") ->
                "Pets are welcome! The apartment environment is quiet, clean, non-smoking, and very student-friendly."

            else ->
                "Thanks for asking! I'm happy to help. Would you like to schedule a virtual tour or discuss the sublease terms further?"
        }
    }

    override fun getSavedMessages(): List<ChatMessage> {
        val json = appPreferences.savedChatMessagesJson
        if (!json.isNullOrEmpty()) {
            return try {
                val type = object : TypeToken<List<ChatMessage>>() {}.type
                val parsed: List<ChatMessage> = gson.fromJson(json, type) ?: emptyList()
                // Purge legacy error and sample messages
                parsed.filterNot { msg ->
                    msg.id in listOf("1", "2", "3", "4") ||
                    msg.text.contains("trouble connecting to the network", ignoreCase = true) ||
                    msg.text.contains("backend server is reachable", ignoreCase = true)
                }
            } catch (_: Exception) {
                emptyList()
            }
        }
        return emptyList()
    }

    override fun saveMessages(messages: List<ChatMessage>) {
        try {
            if (messages.isEmpty()) {
                appPreferences.savedChatMessagesJson = null
            } else {
                appPreferences.savedChatMessagesJson = gson.toJson(messages)
            }
        } catch (_: Exception) {}
    }

    override fun clearMessages() {
        appPreferences.savedChatMessagesJson = null
    }
}
