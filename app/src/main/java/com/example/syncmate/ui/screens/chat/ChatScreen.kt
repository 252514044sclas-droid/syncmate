package com.example.syncmate.ui.screens.chat

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.syncmate.data.local.AppPreferences
import com.example.syncmate.ui.components.AppHeader
import com.example.syncmate.ui.components.AppTextField
import com.example.syncmate.ui.components.EmptyStateView
import com.example.syncmate.ui.theme.EmeraldSuccess
import com.example.syncmate.ui.theme.IndigoPrimary
import com.example.syncmate.ui.theme.SlateBackground
import com.example.syncmate.ui.theme.SlateCard
import com.example.syncmate.ui.theme.TextPrimary
import com.example.syncmate.ui.theme.TextSecondary
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class ChatMessage(
    val id: String,
    val text: String,
    val isMe: Boolean,
    val timestamp: String
)

@Composable
fun ChatScreen(
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    val appPreferences = remember { AppPreferences(context) }
    val gson = remember { Gson() }

    var messageText by remember { mutableStateOf("") }
    var isSubletterTyping by remember { mutableStateOf(false) }

    val defaultMessages = emptyList<ChatMessage>()

    val savedJson = appPreferences.savedChatMessagesJson
    val initialList: List<ChatMessage> = remember(savedJson) {
        if (!savedJson.isNullOrEmpty()) {
            try {
                val type = object : TypeToken<List<ChatMessage>>() {}.type
                val parsed: List<ChatMessage>? = gson.fromJson(savedJson, type)
                if (!parsed.isNullOrEmpty()) {
                    // Purge legacy sample test messages
                    val filtered = parsed.filterNot { msg ->
                        msg.id in listOf("1", "2", "3", "4") ||
                        msg.text.contains("1BHK", ignoreCase = true) ||
                        msg.text.contains("200Mbps", ignoreCase = true) ||
                        msg.text.contains("hiii", ignoreCase = true) ||
                        msg.text.contains("whats an basic amount", ignoreCase = true) ||
                        msg.text.contains("The rent is fixed at", ignoreCase = true) ||
                        msg.text.contains("Hey there!", ignoreCase = true)
                    }
                    filtered
                } else {
                    defaultMessages
                }
            } catch (_: Exception) {
                defaultMessages
            }
        } else {
            defaultMessages
        }
    }

    var messagesList by remember { mutableStateOf(initialList) }
    val coroutineScope = rememberCoroutineScope()
    val listState = rememberLazyListState()

    fun getTimeString(): String {
        return SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Date())
    }

    fun persistMessages(updatedList: List<ChatMessage>) {
        messagesList = updatedList
        try {
            if (updatedList.isEmpty()) {
                appPreferences.savedChatMessagesJson = null
            } else {
                appPreferences.savedChatMessagesJson = gson.toJson(updatedList)
            }
        } catch (_: Exception) {}
    }

    // Force clear old test data on composition launch if needed
    LaunchedEffect(Unit) {
        if (savedJson != null && (savedJson.contains("hiii") || savedJson.contains("whats an basic amount"))) {
            appPreferences.savedChatMessagesJson = null
            messagesList = emptyList()
        }
    }

    fun generateSubletterResponse(userPrompt: String): String {
        val query = userPrompt.lowercase(Locale.getDefault())

        return when {
            query.contains("hi") || query.contains("hello") || query.contains("hey") ->
                "Hey there! 👋 Excited to chat! How can I help you regarding the sublet property or lease details?"

            query.contains("available") || query.contains("dates") || query.contains("duration") || query.contains("when") ->
                "Yes, the sublet is available from May 15 to August 15! The move-in date can also be slightly flexible if your exams finish early."

            query.contains("rent") || query.contains("price") || query.contains("cost") || query.contains("inr") || query.contains("rupees") || query.contains("how much") ->
                "The rent is fixed at ₹14,500/month. We verified this rate with the SyncMate ML Fair-Rent Engine to make sure it's fair for students."

            query.contains("wifi") || query.contains("utility") || query.contains("utilities") || query.contains("electricity") || query.contains("ac") || query.contains("water") ->
                "High-speed WiFi, water, and society maintenance are included! Electricity & AC are metered separately based on consumption."

            query.contains("location") || query.contains("where") || query.contains("address") || query.contains("distance") || query.contains("metro") || query.contains("campus") ->
                "It's located in Adyar, just 0.4 km (a 5-minute walk) from the main university gate and close to the metro station!"

            query.contains("deposit") || query.contains("security") || query.contains("advance") ->
                "The security deposit is ₹15,000. It is fully refundable upon move-out after inspecting the room condition."

            query.contains("visit") || query.contains("tour") || query.contains("see") || query.contains("viewing") || query.contains("show") ->
                "I'd be happy to show you around! We can set up an in-person campus visit or a live video tour. What time works for you?"

            query.contains("furnish") || query.contains("bed") || query.contains("desk") || query.contains("kitchen") || query.contains("fridge") ->
                "The room is fully furnished! It includes a study desk, ergonomic chair, queen bed, spacious wardrobe, refrigerator, and microwave."

            query.contains("pet") || query.contains("smoking") || query.contains("roommate") || query.contains("guest") ->
                "Pets are welcome! The apartment environment is quiet, clean, non-smoking, and very student-friendly."

            else ->
                "Thanks for asking! I'm happy to help. Would you like to schedule a virtual tour or discuss the sublease terms further?"
        }
    }

    fun sendMessage() {
        if (messageText.isNotBlank()) {
            val userMsg = messageText.trim()
            messageText = ""

            val newMsg = ChatMessage(
                id = System.currentTimeMillis().toString(),
                text = userMsg,
                isMe = true,
                timestamp = getTimeString()
            )

            val updatedListWithUser = messagesList + newMsg
            persistMessages(updatedListWithUser)

            // Auto-scroll to latest message
            coroutineScope.launch {
                delay(100)
                listState.animateScrollToItem(updatedListWithUser.size - 1)
            }

            // Simulate intelligent subletter response delay
            isSubletterTyping = true
            coroutineScope.launch {
                delay(1200)
                isSubletterTyping = false

                val responseText = generateSubletterResponse(userMsg)
                val replyMsg = ChatMessage(
                    id = (System.currentTimeMillis() + 1).toString(),
                    text = responseText,
                    isMe = false,
                    timestamp = getTimeString()
                )

                val updatedListWithReply = messagesList + replyMsg
                persistMessages(updatedListWithReply)

                delay(100)
                listState.animateScrollToItem(updatedListWithReply.size - 1)
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SlateBackground)
            .safeDrawingPadding()
    ) {
        AppHeader(
            title = "Chat with Subletter (Sarah)",
            onBackClick = onNavigateBack,
            action = {
                if (messagesList.isNotEmpty()) {
                    IconButton(onClick = { persistMessages(emptyList()) }) {
                        Icon(
                            imageVector = Icons.Default.DeleteSweep,
                            contentDescription = "Clear Chat",
                            tint = TextPrimary
                        )
                    }
                }
            }
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .imePadding()
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            if (messagesList.isEmpty() && !isSubletterTyping) {
                Box(
                    modifier = Modifier.weight(1f).fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    EmptyStateView(
                        title = "Start a Conversation",
                        description = "Type your message below to inquire about the sublet, rent, or schedule a tour."
                    )
                }
            } else {
                LazyColumn(
                    state = listState,
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(messagesList) { msg ->
                        Box(
                            modifier = Modifier.fillMaxWidth(),
                            contentAlignment = if (msg.isMe) Alignment.CenterEnd else Alignment.CenterStart
                        ) {
                            Column(
                                horizontalAlignment = if (msg.isMe) Alignment.End else Alignment.Start
                            ) {
                                Box(
                                    modifier = Modifier
                                        .clip(
                                            RoundedCornerShape(
                                                topStart = 16.dp,
                                                topEnd = 16.dp,
                                                bottomStart = if (msg.isMe) 16.dp else 4.dp,
                                                bottomEnd = if (msg.isMe) 4.dp else 16.dp
                                            )
                                        )
                                        .background(if (msg.isMe) IndigoPrimary else SlateCard)
                                        .padding(horizontal = 14.dp, vertical = 10.dp)
                                ) {
                                    Text(
                                        text = msg.text,
                                        color = TextPrimary,
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                }

                                Text(
                                    text = msg.timestamp,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = TextSecondary,
                                    modifier = Modifier.padding(top = 2.dp, start = 4.dp, end = 4.dp)
                                )
                            }
                        }
                    }

                    if (isSubletterTyping) {
                        item {
                            Box(
                                modifier = Modifier.fillMaxWidth(),
                                contentAlignment = Alignment.CenterStart
                            ) {
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(SlateCard)
                                        .padding(horizontal = 12.dp, vertical = 8.dp)
                                ) {
                                    Text(
                                        text = "Sarah is typing...",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = EmeraldSuccess,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp)
            ) {
                AppTextField(
                    value = messageText,
                    onValueChange = { messageText = it },
                    label = "",
                    placeholder = "Type message...",
                    singleLine = true,
                    modifier = Modifier.weight(1f)
                )

                Spacer(modifier = Modifier.width(8.dp))

                IconButton(
                    onClick = { sendMessage() }
                ) {
                    Icon(imageVector = Icons.AutoMirrored.Filled.Send, contentDescription = "Send", tint = IndigoPrimary)
                }
            }
        }
    }
}
