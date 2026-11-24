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
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Inbox
import androidx.compose.material.icons.filled.Outbox
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.syncmate.data.firestore.SyncMateFirestore
import com.example.syncmate.data.model.RoommateRequest
import com.example.syncmate.ui.components.GlassCard
import com.example.syncmate.ui.components.PrimaryButton
import com.example.syncmate.ui.components.SecondaryButton
import com.example.syncmate.ui.components.StatBadge
import com.example.syncmate.ui.theme.AmberWarning
import com.example.syncmate.ui.theme.EmeraldSuccess
import com.example.syncmate.ui.theme.IndigoPrimary
import com.example.syncmate.ui.theme.RoseError
import com.example.syncmate.ui.theme.SlateBackground
import com.example.syncmate.ui.theme.SlateCard
import com.example.syncmate.ui.theme.SlateCardBorder
import com.example.syncmate.ui.theme.TextPrimary
import com.example.syncmate.ui.theme.TextSecondary
import com.example.syncmate.util.CurrencyFormatter
import com.example.syncmate.viewmodel.RoommateRequestsViewModel
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun RoommateRequestsScreen(
    viewModel: RoommateRequestsViewModel,
    onNavigateToProfile: (String) -> Unit,
    onNavigateToChat: () -> Unit,
    onNavigateBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val haptic = LocalHapticFeedback.current

    val tabTitles = listOf("Incoming Requests", "Sent Requests")

    // Auto-dismiss notification toast message after 3.5 seconds
    LaunchedEffect(uiState.actionMessage) {
        if (uiState.actionMessage != null) {
            delay(3500)
            viewModel.clearActionMessage()
        }
    }

    LaunchedEffect(uiState.errorMessage) {
        if (uiState.errorMessage != null) {
            delay(4000)
            viewModel.clearErrorMessage()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SlateBackground)
            .safeDrawingPadding()
    ) {
        // Top-Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onNavigateBack) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
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
                    text = "${uiState.incomingRequests.size} incoming • ${uiState.sentRequests.size} sent",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextSecondary
                )
            }
        }

        // Notification Banner: Action Confirmation
        if (uiState.actionMessage != null) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 4.dp)
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

        // Error Banner: Firestore / Network errors
        if (uiState.errorMessage != null) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 4.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(RoseError.copy(alpha = 0.2f))
                    .border(1.dp, RoseError.copy(alpha = 0.5f), RoundedCornerShape(10.dp))
                    .padding(horizontal = 14.dp, vertical = 8.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = null,
                        tint = RoseError,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = uiState.errorMessage!!,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = RoseError
                    )
                }
            }
        }

        // Material 3 Two-Tab Header
        TabRow(
            selectedTabIndex = uiState.selectedTab,
            containerColor = SlateCard,
            contentColor = IndigoPrimary,
            indicator = { tabPositions ->
                TabRowDefaults.SecondaryIndicator(
                    modifier = Modifier.tabIndicatorOffset(tabPositions[uiState.selectedTab]),
                    color = IndigoPrimary
                )
            }
        ) {
            tabTitles.forEachIndexed { index, title ->
                Tab(
                    selected = uiState.selectedTab == index,
                    onClick = {
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        viewModel.selectTab(index)
                    },
                    text = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = if (index == 0) Icons.Default.Inbox else Icons.Default.Outbox,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = title,
                                fontWeight = if (uiState.selectedTab == index) FontWeight.Bold else FontWeight.Medium
                            )

                            if (index == 0 && uiState.pendingIncomingCount > 0) {
                                Spacer(modifier = Modifier.width(6.dp))
                                Box(
                                    modifier = Modifier
                                        .clip(CircleShape)
                                        .background(EmeraldSuccess)
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = uiState.pendingIncomingCount.toString(),
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                }
                            }
                        }
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        if (uiState.isLoading && uiState.incomingRequests.isEmpty() && uiState.sentRequests.isEmpty()) {
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
                        text = "Loading real-time requests from Firestore...",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondary
                    )
                }
            }
        } else {
            // Content
            if (uiState.selectedTab == 0) {
                IncomingRequestsTabContent(
                    requests = uiState.incomingRequests,
                    onAccept = { reqId ->
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        viewModel.acceptRequest(reqId)
                    },
                    onDecline = { reqId ->
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        viewModel.declineRequest(reqId)
                    },
                    onViewProfile = onNavigateToProfile,
                    onOpenChat = onNavigateToChat
                )
            } else {
                SentRequestsTabContent(
                    requests = uiState.sentRequests,
                    onCancelRequest = { reqId ->
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        viewModel.cancelRequest(reqId)
                    },
                    onViewProfile = onNavigateToProfile,
                    onOpenChat = onNavigateToChat
                )
            }
        }
    }
}

@Composable
fun IncomingRequestsTabContent(
    requests: List<RoommateRequest>,
    onAccept: (String) -> Unit,
    onDecline: (String) -> Unit,
    onViewProfile: (String) -> Unit,
    onOpenChat: () -> Unit
) {
    if (requests.isEmpty()) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    imageVector = Icons.Default.Inbox,
                    contentDescription = null,
                    tint = TextSecondary,
                    modifier = Modifier.size(56.dp)
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "No Incoming Requests",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "When other students want to connect as roommates, their connection requests will appear here in real time.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }
        }
    } else {
        LazyColumn(
            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(requests, key = { it.requestId }) { req ->
                val senderUser = SyncMateFirestore.getUser(req.senderId)
                val prefs = senderUser?.roommatePreferences
                val budget = prefs?.maxBudgetINR ?: (senderUser?.maxBudget ?: 15000.0)

                GlassCard(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(18.dp)) {
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
                                        contentDescription = req.senderName,
                                        tint = IndigoPrimary,
                                        modifier = Modifier.size(28.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = req.senderName,
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.Bold,
                                            color = TextPrimary
                                        )
                                        if (senderUser?.isVerified != false) {
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
                                        text = req.senderUniversity,
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

                        // Top Matching Lifestyle Traits
                        Spacer(modifier = Modifier.height(10.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            if (prefs != null) {
                                LifestylePill(text = prefs.sleepSchedule)
                                LifestylePill(text = "Clean: ${prefs.cleanlinessRating}/5")
                            }
                            LifestylePill(text = CurrencyFormatter.formatMonthlyRent(budget))
                        }

                        if (req.introMessage.isNotBlank()) {
                            Spacer(modifier = Modifier.height(12.dp))
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(SlateCardBorder.copy(alpha = 0.4f))
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

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = formatTimestamp(req.timestamp),
                                style = MaterialTheme.typography.labelSmall,
                                color = TextSecondary
                            )

                            when (req.status) {
                                "ACCEPTED" -> {
                                    Text(
                                        text = "✓ Accepted • Chat Unlocked",
                                        style = MaterialTheme.typography.labelMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = EmeraldSuccess
                                    )
                                }
                                "DECLINED" -> {
                                    Text(
                                        text = "Declined",
                                        style = MaterialTheme.typography.labelMedium,
                                        color = RoseError
                                    )
                                }
                                else -> {
                                    Text(
                                        text = "Pending Decision",
                                        style = MaterialTheme.typography.labelMedium,
                                        fontWeight = FontWeight.SemiBold,
                                        color = AmberWarning
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        when (req.status) {
                            "PENDING" -> {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    PrimaryButton(
                                        text = "Accept",
                                        onClick = { onAccept(req.requestId) },
                                        modifier = Modifier.weight(1f)
                                    )
                                    SecondaryButton(
                                        text = "Decline",
                                        onClick = { onDecline(req.requestId) },
                                        modifier = Modifier.weight(1f)
                                    )
                                    SecondaryButton(
                                        text = "Profile",
                                        onClick = { onViewProfile(req.senderId) },
                                        modifier = Modifier.width(80.dp)
                                    )
                                }
                            }
                            "ACCEPTED" -> {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    PrimaryButton(
                                        text = "Matched • Open Chat",
                                        onClick = onOpenChat,
                                        modifier = Modifier.weight(1f)
                                    )
                                    SecondaryButton(
                                        text = "Profile",
                                        onClick = { onViewProfile(req.senderId) },
                                        modifier = Modifier.width(90.dp)
                                    )
                                }
                            }
                            else -> {
                                SecondaryButton(
                                    text = "View Profile",
                                    onClick = { onViewProfile(req.senderId) },
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SentRequestsTabContent(
    requests: List<RoommateRequest>,
    onCancelRequest: (String) -> Unit,
    onViewProfile: (String) -> Unit,
    onOpenChat: () -> Unit
) {
    if (requests.isEmpty()) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    imageVector = Icons.Default.Outbox,
                    contentDescription = null,
                    tint = TextSecondary,
                    modifier = Modifier.size(56.dp)
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "No Sent Requests",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Browse AI compatible roommates in the feed and tap 'Send Roommate Request' to connect.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }
        }
    } else {
        LazyColumn(
            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(requests, key = { it.requestId }) { req ->
                val receiverUser = SyncMateFirestore.getUser(req.receiverId)

                GlassCard(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(18.dp)) {
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
                                        contentDescription = req.receiverName,
                                        tint = IndigoPrimary,
                                        modifier = Modifier.size(28.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = req.receiverName,
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = TextPrimary
                                    )
                                    Text(
                                        text = if (req.receiverUniversity.isNotBlank()) req.receiverUniversity else (receiverUser?.university ?: "Student Peer"),
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

                        if (req.introMessage.isNotBlank()) {
                            Spacer(modifier = Modifier.height(12.dp))
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(SlateCardBorder.copy(alpha = 0.4f))
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

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = formatTimestamp(req.timestamp),
                                style = MaterialTheme.typography.labelSmall,
                                color = TextSecondary
                            )

                            // Live Status Badge
                            when (req.status) {
                                "ACCEPTED" -> {
                                    Text(
                                        text = "✓ Accepted! Direct Chat Unlocked",
                                        style = MaterialTheme.typography.labelMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = EmeraldSuccess
                                    )
                                }
                                "DECLINED" -> {
                                    Text(
                                        text = "Declined",
                                        style = MaterialTheme.typography.labelMedium,
                                        fontWeight = FontWeight.SemiBold,
                                        color = RoseError
                                    )
                                }
                                else -> {
                                    Text(
                                        text = "Pending Approval",
                                        style = MaterialTheme.typography.labelMedium,
                                        fontWeight = FontWeight.SemiBold,
                                        color = AmberWarning
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            when (req.status) {
                                "ACCEPTED" -> {
                                    PrimaryButton(
                                        text = "Matched • Open Chat",
                                        onClick = onOpenChat,
                                        modifier = Modifier.weight(1f)
                                    )
                                    SecondaryButton(
                                        text = "View Profile",
                                        onClick = { onViewProfile(req.receiverId) },
                                        modifier = Modifier.width(110.dp)
                                    )
                                }
                                "PENDING" -> {
                                    SecondaryButton(
                                        text = "Cancel Request",
                                        onClick = { onCancelRequest(req.requestId) },
                                        modifier = Modifier.weight(1f)
                                    )
                                    SecondaryButton(
                                        text = "View Profile",
                                        onClick = { onViewProfile(req.receiverId) },
                                        modifier = Modifier.weight(1f)
                                    )
                                }
                                else -> {
                                    SecondaryButton(
                                        text = "View Profile",
                                        onClick = { onViewProfile(req.receiverId) },
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun LifestylePill(text: String) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(IndigoPrimary.copy(alpha = 0.12f))
            .border(1.dp, IndigoPrimary.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.SemiBold,
            color = IndigoPrimary
        )
    }
}

fun formatTimestamp(timestamp: Long): String {
    val diff = System.currentTimeMillis() - timestamp
    val minutes = diff / (1000 * 60)
    val hours = minutes / 60
    val days = hours / 24

    return when {
        diff < 0 -> "Just now"
        minutes < 2 -> "Just now"
        minutes < 60 -> "$minutes min ago"
        hours < 24 -> "$hours hr ago"
        days == 1L -> "Yesterday"
        else -> SimpleDateFormat("MMM d, yyyy", Locale.getDefault()).format(Date(timestamp))
    }
}
