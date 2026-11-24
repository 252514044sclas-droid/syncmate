package com.example.syncmate.ui.screens.roommates

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Inbox
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.syncmate.ui.components.GlassCard
import com.example.syncmate.ui.components.PrimaryButton
import com.example.syncmate.ui.components.SecondaryButton
import com.example.syncmate.ui.components.StatBadge
import com.example.syncmate.ui.theme.EmeraldSuccess
import com.example.syncmate.ui.theme.IndigoPrimary
import com.example.syncmate.ui.theme.RoseError
import com.example.syncmate.ui.theme.SlateBackground
import com.example.syncmate.ui.theme.SlateCard
import com.example.syncmate.ui.theme.SlateCardBorder
import com.example.syncmate.ui.theme.TextPrimary
import com.example.syncmate.ui.theme.TextSecondary
import com.example.syncmate.util.CurrencyFormatter
import com.example.syncmate.viewmodel.IncomingRequestUiModel
import com.example.syncmate.viewmodel.IncomingRequestsViewModel
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun IncomingRequestsScreen(
    viewModel: IncomingRequestsViewModel,
    onNavigateToProfile: (String) -> Unit,
    onNavigateToChat: (chatId: String, otherUserId: String, otherUserName: String) -> Unit,
    onNavigateBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val haptic = LocalHapticFeedback.current

    // Auto-dismiss notification toast message after 3 seconds
    LaunchedEffect(uiState.actionMessage) {
        if (uiState.actionMessage != null) {
            delay(3000)
            viewModel.clearActionMessage()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SlateBackground)
            .safeDrawingPadding()
    ) {
        // Material 3 Top App Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onNavigateBack) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = TextPrimary
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text(
                    text = "Roommate Connection Requests",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Text(
                    text = "${uiState.incomingRequests.size} incoming • ${uiState.sentRequests.size} sent from device",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextSecondary
                )
            }
        }

        // Tab Selector Row: Incoming vs Sent Requests
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            val isIncomingSelected = uiState.selectedTab == "Incoming"
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(12.dp))
                    .background(if (isIncomingSelected) IndigoPrimary else SlateCard)
                    .border(
                        width = 1.dp,
                        color = if (isIncomingSelected) IndigoPrimary else SlateCardBorder,
                        shape = RoundedCornerShape(12.dp)
                    )
                    .clickable {
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        viewModel.setSelectedTab("Incoming")
                    }
                    .padding(vertical = 10.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Inbox,
                        contentDescription = null,
                        tint = if (isIncomingSelected) MaterialTheme.colorScheme.onPrimary else TextPrimary,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Incoming (${uiState.incomingRequests.size})",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = if (isIncomingSelected) MaterialTheme.colorScheme.onPrimary else TextPrimary
                    )
                }
            }

            val isSentSelected = uiState.selectedTab == "Sent"
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(12.dp))
                    .background(if (isSentSelected) IndigoPrimary else SlateCard)
                    .border(
                        width = 1.dp,
                        color = if (isSentSelected) IndigoPrimary else SlateCardBorder,
                        shape = RoundedCornerShape(12.dp)
                    )
                    .clickable {
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        viewModel.setSelectedTab("Sent")
                    }
                    .padding(vertical = 10.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Send,
                        contentDescription = null,
                        tint = if (isSentSelected) MaterialTheme.colorScheme.onPrimary else TextPrimary,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Sent (${uiState.sentRequests.size})",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = if (isSentSelected) MaterialTheme.colorScheme.onPrimary else TextPrimary
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        // Action Message Toast Banner
        if (uiState.actionMessage != null) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 6.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(EmeraldSuccess.copy(alpha = 0.2f))
                    .border(1.dp, EmeraldSuccess.copy(alpha = 0.5f), RoundedCornerShape(10.dp))
                    .padding(horizontal = 14.dp, vertical = 8.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = EmeraldSuccess,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = uiState.actionMessage!!,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = EmeraldSuccess
                    )
                }
            }
        }

        // Body Content
        if (uiState.isLoading && uiState.requests.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    CircularProgressIndicator(color = IndigoPrimary)
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Loading real-time connection requests...",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondary
                    )
                }
            }
        } else if (uiState.requests.isEmpty()) {
            // Clean Empty State
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f)
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box(
                        modifier = Modifier
                            .size(72.dp)
                            .clip(CircleShape)
                            .background(IndigoPrimary.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (uiState.selectedTab == "Sent") Icons.Default.Send else Icons.Default.Inbox,
                            contentDescription = null,
                            tint = IndigoPrimary,
                            modifier = Modifier.size(36.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = if (uiState.selectedTab == "Sent") "No Sent Requests Yet" else "No Pending Incoming Requests",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = if (uiState.selectedTab == "Sent")
                            "When you send connection requests to roommate candidates, they will appear here with live status tracking."
                        else
                            "When other verified university students discover your profile and want to connect as roommates, their requests will appear here in real time.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondary,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                }
            }
        } else {
            // Real-Time Requests List
            LazyColumn(
                contentPadding = PaddingValues(horizontal = 20.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.weight(1f)
            ) {
                items(uiState.requests, key = { it.request.requestId }) { item ->
                    IncomingRequestCard(
                        item = item,
                        isSentTab = uiState.selectedTab == "Sent",
                        onViewProfile = { onNavigateToProfile(if (uiState.selectedTab == "Sent") item.request.receiverId else item.request.senderId) },
                        onAccept = {
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            viewModel.acceptRequest(item.request.requestId)
                        },
                        onDecline = {
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            viewModel.declineRequest(item.request.requestId)
                        },
                        onOpenChat = {
                            val otherId = if (uiState.selectedTab == "Sent") item.request.receiverId else item.request.senderId
                            val otherName = if (uiState.selectedTab == "Sent") item.request.receiverName else item.request.senderName
                            val chatId = listOf(item.request.senderId, item.request.receiverId).sorted().joinToString("_")
                            onNavigateToChat(chatId, otherId, otherName)
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun IncomingRequestCard(
    item: IncomingRequestUiModel,
    isSentTab: Boolean,
    onViewProfile: () -> Unit,
    onAccept: () -> Unit,
    onDecline: () -> Unit,
    onOpenChat: () -> Unit
) {
    val req = item.request
    val displayName = if (isSentTab) req.receiverName else req.senderName
    val displayUni = if (isSentTab) {
        if (req.receiverUniversity.isNotBlank()) req.receiverUniversity else "Sent to Campus Partner"
    } else {
        "${req.senderUniversity} • .edu Verified"
    }

    GlassCard(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            // Header Row: Sender/Receiver Details & Neural Compatibility Score
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .clip(CircleShape)
                            .background(IndigoPrimary.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = displayName,
                            tint = IndigoPrimary,
                            modifier = Modifier.size(28.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = displayName,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            if (item.isVerified) {
                                Spacer(modifier = Modifier.width(4.dp))
                                Icon(
                                    imageVector = Icons.Default.VerifiedUser,
                                    contentDescription = "Verified Student",
                                    tint = EmeraldSuccess,
                                    modifier = Modifier.size(15.dp)
                                )
                            }
                        }

                        Text(
                            text = displayUni,
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary
                        )
                    }
                }

                StatBadge(
                    text = "${req.compatibilityScore}% MATCH",
                    backgroundColor = EmeraldSuccess
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Status Badge for Sent Requests
            if (isSentTab) {
                val statusText = when (req.status) {
                    "ACCEPTED" -> "✓ ACCEPTED • CHAT UNLOCKED"
                    "DECLINED" -> "DECLINED"
                    else -> "⏳ CONNECTION PENDING"
                }
                val statusColor = when (req.status) {
                    "ACCEPTED" -> EmeraldSuccess
                    "DECLINED" -> RoseError
                    else -> IndigoPrimary
                }
                StatBadge(text = statusText, backgroundColor = statusColor)
                Spacer(modifier = Modifier.height(10.dp))
            } else {
                // Top Matching Lifestyle Traits (8-Dimension Preview)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    LifestyleTraitPill(label = "Rhythm", value = item.sleepSchedule)
                    LifestyleTraitPill(label = "Cleanliness", value = "${item.cleanlinessRating}/5")
                    LifestyleTraitPill(label = "Budget", value = CurrencyFormatter.formatMonthlyRent(item.budgetINR))
                }
            }

            // Intro Note Sent With Request
            if (req.introMessage.isNotBlank()) {
                Spacer(modifier = Modifier.height(12.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(SlateCard.copy(alpha = 0.8f))
                        .border(1.dp, SlateCardBorder, RoundedCornerShape(8.dp))
                        .padding(12.dp)
                ) {
                    Text(
                        text = "\"${req.introMessage}\"",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextPrimary
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Timestamp
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                Text(
                    text = formatRequestTime(req.timestamp),
                    style = MaterialTheme.typography.labelSmall,
                    color = TextSecondary
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Actions
            if (isSentTab) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (req.status == "ACCEPTED") {
                        PrimaryButton(
                            text = "Chat Now",
                            onClick = onOpenChat,
                            modifier = Modifier.weight(1f)
                        )
                    }
                    SecondaryButton(
                        text = "View Profile",
                        onClick = onViewProfile,
                        modifier = Modifier.weight(1f)
                    )
                }
            } else {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    PrimaryButton(
                        text = "Accept",
                        onClick = onAccept,
                        modifier = Modifier.weight(1.2f)
                    )

                    SecondaryButton(
                        text = "Decline",
                        onClick = onDecline,
                        modifier = Modifier.weight(1f)
                    )

                    SecondaryButton(
                        text = "View Profile",
                        onClick = onViewProfile,
                        modifier = Modifier.weight(1.2f)
                    )
                }
            }
        }
    }
}

@Composable
fun LifestyleTraitPill(label: String, value: String) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(SlateCardBorder.copy(alpha = 0.4f))
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Column {
            Text(label, style = MaterialTheme.typography.labelSmall, color = TextSecondary)
            Text(value, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = TextPrimary)
        }
    }
}

fun formatRequestTime(timestamp: Long): String {
    val diff = System.currentTimeMillis() - timestamp
    val minutes = diff / (1000 * 60)
    val hours = minutes / 60
    val days = hours / 24

    return when {
        minutes < 2 -> "Just now"
        minutes < 60 -> "$minutes mins ago"
        hours < 24 -> "$hours hours ago"
        days == 1L -> "Yesterday"
        else -> SimpleDateFormat("MMM d, yyyy", Locale.getDefault()).format(Date(timestamp))
    }
}
