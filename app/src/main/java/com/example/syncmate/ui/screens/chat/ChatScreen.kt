package com.example.syncmate.ui.screens.chat

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.syncmate.ui.components.AppHeader
import com.example.syncmate.ui.components.EmptyStateView
import com.example.syncmate.ui.theme.EmeraldSuccess
import com.example.syncmate.ui.theme.IndigoPrimary
import com.example.syncmate.ui.theme.SlateBackground
import com.example.syncmate.ui.theme.SlateCard
import com.example.syncmate.ui.theme.SlateCardBorder
import com.example.syncmate.ui.theme.TextPrimary
import com.example.syncmate.ui.theme.TextSecondary
import com.example.syncmate.viewmodel.ChatViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun ChatScreen(
    chatViewModel: ChatViewModel,
    onNavigateBack: () -> Unit
) {
    val uiState by chatViewModel.uiState.collectAsStateWithLifecycle()
    var messageText by remember { mutableStateOf("") }
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    // Auto-scroll to bottom whenever messages or typing state updates
    LaunchedEffect(uiState.messages.size, uiState.isTyping) {
        if (uiState.messages.isNotEmpty()) {
            delay(50)
            listState.animateScrollToItem(uiState.messages.size - 1)
        }
    }

    fun handleSend() {
        if (messageText.isNotBlank()) {
            val textToSend = messageText
            messageText = ""
            chatViewModel.sendMessage(textToSend)
            coroutineScope.launch {
                delay(100)
                if (uiState.messages.isNotEmpty()) {
                    listState.animateScrollToItem(uiState.messages.size - 1)
                }
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
            title = "SyncMate AI Housing Assistant",
            onBackClick = onNavigateBack,
            action = {
                if (uiState.messages.isNotEmpty()) {
                    IconButton(onClick = { chatViewModel.clearChat() }) {
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
            if (uiState.messages.isEmpty() && !uiState.isTyping) {
                Box(
                    modifier = Modifier.weight(1f).fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    EmptyStateView(
                        title = "Ask SyncMate AI",
                        description = "Inquire about fair rents in INR (₹), student sublet availability, roommate matching, or campus housing safety tips."
                    )
                }
            } else {
                LazyColumn(
                    state = listState,
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(uiState.messages) { msg ->
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

                    if (uiState.isTyping) {
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
                                        text = "SyncMate AI is thinking...",
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
                OutlinedTextField(
                    value = messageText,
                    onValueChange = { messageText = it },
                    placeholder = { Text("Ask about rent, roommates, sublets...", color = TextSecondary) },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                    keyboardActions = KeyboardActions(onSend = { handleSend() }),
                    shape = RoundedCornerShape(24.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = SlateCard,
                        unfocusedContainerColor = SlateCard,
                        focusedBorderColor = IndigoPrimary,
                        unfocusedBorderColor = SlateCardBorder,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary,
                        cursorColor = IndigoPrimary
                    ),
                    modifier = Modifier.weight(1f)
                )

                Spacer(modifier = Modifier.width(10.dp))

                Box(
                    modifier = Modifier
                        .size(50.dp)
                        .clip(CircleShape)
                        .background(if (messageText.isNotBlank()) IndigoPrimary else IndigoPrimary.copy(alpha = 0.4f))
                        .clickable(enabled = messageText.isNotBlank()) { handleSend() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Send,
                        contentDescription = "Send Message",
                        tint = Color.White,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }
        }
    }
}
