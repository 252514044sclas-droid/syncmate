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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.syncmate.data.model.RoommateMatchProfile
import com.example.syncmate.data.model.User
import com.example.syncmate.ui.components.GlassCard
import com.example.syncmate.ui.components.PrimaryButton
import com.example.syncmate.ui.components.SecondaryButton
import com.example.syncmate.ui.components.StatBadge
import com.example.syncmate.ui.theme.EmeraldGreenPrimary
import com.example.syncmate.ui.theme.EmeraldSuccess
import com.example.syncmate.ui.theme.IndigoPrimary
import com.example.syncmate.ui.theme.SlateBackground
import com.example.syncmate.ui.theme.SlateCard
import com.example.syncmate.ui.theme.SlateCardBorder
import com.example.syncmate.ui.theme.TextPrimary
import com.example.syncmate.ui.theme.TextSecondary
import com.example.syncmate.util.CurrencyFormatter
import com.example.syncmate.viewmodel.RoommateMatchViewModel

@Composable
fun RoommatesScreen(
    viewModel: RoommateMatchViewModel,
    onNavigateToProfile: (String) -> Unit,
    onNavigateToRequests: () -> Unit,
    onNavigateToChat: (chatId: String, otherUserId: String, otherUserName: String) -> Unit,
    onNavigateToQuiz: () -> Unit,
    onNavigateBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val haptic = LocalHapticFeedback.current

    LaunchedEffect(uiState.actionMessage) {
        if (uiState.actionMessage != null) {
            kotlinx.coroutines.delay(3500)
            viewModel.clearActionMessage()
        }
    }

    LaunchedEffect(uiState.errorMessage) {
        if (uiState.errorMessage != null) {
            kotlinx.coroutines.delay(4000)
            viewModel.clearErrorMessage()
        }
    }

    var showIntroDialogForUser by remember { mutableStateOf<User?>(null) }
    var introMessageText by remember { mutableStateOf("") }

    val filterOptions = listOf("All Matches", "90%+ Synergy", "Early Riser", "Quiet Study")

    val filteredList = remember(uiState.selectedFilter, uiState.roommates) {
        when (uiState.selectedFilter) {
            "90%+ Synergy" -> uiState.roommates.filter { it.compatibilityPercentage >= 90 }
            "Early Riser" -> uiState.roommates.filter {
                it.user.roommatePreferences?.sleepSchedule?.contains("Early", ignoreCase = true) == true
            }
            "Quiet Study" -> uiState.roommates.filter {
                it.user.roommatePreferences?.noiseTolerance?.contains("Quiet", ignoreCase = true) == true
            }
            else -> uiState.roommates
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SlateBackground)
            .safeDrawingPadding()
    ) {
        // Custom Top-Bar with Live Requests Counter Badge
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onNavigateBack) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = TextPrimary
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "AI Compatible Roommates",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
            }

            // Clearly Visible Connection Requests Button with Live BadgedBox
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(if (uiState.pendingIncomingCount > 0) EmeraldSuccess.copy(alpha = 0.15f) else SlateCard)
                    .border(
                        1.dp,
                        if (uiState.pendingIncomingCount > 0) EmeraldSuccess.copy(alpha = 0.5f) else SlateCardBorder,
                        RoundedCornerShape(20.dp)
                    )
                    .clickable {
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        onNavigateToRequests()
                    }
                    .padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    BadgedBox(
                        badge = {
                            if (uiState.pendingIncomingCount > 0) {
                                Badge(
                                    containerColor = EmeraldSuccess,
                                    contentColor = Color.White
                                ) {
                                    Text(
                                        text = uiState.pendingIncomingCount.toString(),
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Group,
                            contentDescription = "Roommate Requests",
                            tint = if (uiState.pendingIncomingCount > 0) EmeraldSuccess else TextPrimary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Requests",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = if (uiState.pendingIncomingCount > 0) EmeraldSuccess else TextPrimary
                    )
                }
            }
        }

        // Action Confirmation Banner
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

        // Error Banner
        if (uiState.errorMessage != null) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 4.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(com.example.syncmate.ui.theme.RoseError.copy(alpha = 0.2f))
                    .border(1.dp, com.example.syncmate.ui.theme.RoseError.copy(alpha = 0.5f), RoundedCornerShape(10.dp))
                    .padding(horizontal = 14.dp, vertical = 8.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = null,
                        tint = com.example.syncmate.ui.theme.RoseError,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = uiState.errorMessage!!,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = com.example.syncmate.ui.theme.RoseError
                    )
                }
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Real-Time Roommates",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )

                    Text(
                        text = "Live neural compatibility scored via our self-trained MLPRegressor.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondary,
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }

                Text(
                    text = "Retake Quiz",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    color = IndigoPrimary,
                    modifier = Modifier
                        .clickable { onNavigateToQuiz() }
                        .padding(start = 8.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Filter Chips
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(bottom = 16.dp)
            ) {
                items(filterOptions) { filter ->
                    val isSelected = filter == uiState.selectedFilter
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(if (isSelected) IndigoPrimary else SlateCard)
                            .border(
                                width = 1.dp,
                                color = if (isSelected) IndigoPrimary else SlateCardBorder,
                                shape = RoundedCornerShape(20.dp)
                            )
                            .clickable {
                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                viewModel.setFilter(filter)
                            }
                            .padding(horizontal = 14.dp, vertical = 8.dp)
                    ) {
                        Text(
                            text = filter,
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            color = if (isSelected) MaterialTheme.colorScheme.onPrimary else TextPrimary
                        )
                    }
                }
            }

            if (uiState.isLoading && uiState.roommates.isEmpty()) {
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
                            text = "Calculating live neural synergy scores...",
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextSecondary
                        )
                    }
                }
            } else if (filteredList.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(24.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Group,
                            contentDescription = null,
                            tint = TextSecondary,
                            modifier = Modifier.size(56.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "No Roommates Discovered Yet",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "As new students join and complete the 10-question Onboarding Quiz, they will appear here in real time.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextSecondary,
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )
                    }
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(bottom = 24.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    items(filteredList, key = { it.user.id }) { roommate ->
                        RoommateCard(
                            roommate = roommate,
                            onCardClick = { onNavigateToProfile(roommate.user.id) },
                            onRequestClick = {
                                when (roommate.requestStatus) {
                                    "NONE" -> {
                                        showIntroDialogForUser = roommate.user
                                        introMessageText = "Hi ${roommate.user.name}, let's team up for a sublet near campus!"
                                    }
                                    "PENDING_RECEIVED" -> {
                                        onNavigateToRequests()
                                    }
                                    "ACCEPTED" -> {
                                        val chatId = viewModel.getChatIdForCandidate(roommate.user.id)
                                        onNavigateToChat(chatId, roommate.user.id, roommate.user.name)
                                    }
                                }
                            },
                            onChatClick = {
                                val chatId = viewModel.getChatIdForCandidate(roommate.user.id)
                                onNavigateToChat(chatId, roommate.user.id, roommate.user.name)
                            }
                        )
                    }
                }
            }
        }
    }

    // Intro Message Dialog
    if (showIntroDialogForUser != null) {
        val targetUser = showIntroDialogForUser!!
        AlertDialog(
            onDismissRequest = { showIntroDialogForUser = null },
            title = {
                Text(
                    text = "Send Roommate Request",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
            },
            text = {
                Column {
                    Text(
                        text = "Introduce yourself to ${targetUser.name} (${targetUser.university}):",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondary
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = introMessageText,
                        onValueChange = { introMessageText = it },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("Add an intro note...", color = TextSecondary) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = IndigoPrimary,
                            unfocusedBorderColor = SlateCardBorder,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary
                        ),
                        maxLines = 4
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        viewModel.sendRoommateRequest(targetUser, introMessageText)
                        showIntroDialogForUser = null
                    }
                ) {
                    Text("Send Request", color = IndigoPrimary, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showIntroDialogForUser = null }) {
                    Text("Cancel", color = TextSecondary)
                }
            },
            containerColor = SlateCard
        )
    }
}

@Composable
fun RoommateCard(
    roommate: RoommateMatchProfile,
    onCardClick: () -> Unit,
    onRequestClick: () -> Unit,
    onChatClick: () -> Unit
) {
    val budgetAmount = roommate.user.roommatePreferences?.maxBudgetINR ?: roommate.user.maxBudget

    GlassCard(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onCardClick() }
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(50.dp)
                            .clip(CircleShape)
                            .background(IndigoPrimary.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = roommate.user.name,
                            tint = IndigoPrimary,
                            modifier = Modifier.size(30.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = roommate.user.name,
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            if (roommate.user.isVerified) {
                                Spacer(modifier = Modifier.width(4.dp))
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = "Verified University Student",
                                    tint = EmeraldSuccess,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                        Text(
                            text = roommate.user.university,
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextSecondary
                        )
                    }
                }

                StatBadge(
                    text = "${roommate.compatibilityPercentage}% SYNERGY",
                    backgroundColor = EmeraldSuccess
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "💡 " + roommate.matchReason,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.SemiBold,
                color = IndigoPrimary
            )

            if (roommate.user.bio.isNotBlank()) {
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = roommate.user.bio,
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextPrimary
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Budget: ${CurrencyFormatter.formatMonthlyRent(budgetAmount)}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = IndigoPrimary
                )

                Text(
                    text = "Area: ${roommate.user.preferredLocation}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                val buttonText = when (roommate.requestStatus) {
                    "PENDING_SENT" -> "✓ Request Sent"
                    "PENDING_RECEIVED" -> "Review Request"
                    "ACCEPTED" -> "Connected • Chat"
                    else -> "Send Roommate Request"
                }

                PrimaryButton(
                    text = buttonText,
                    onClick = onRequestClick,
                    enabled = roommate.requestStatus != "PENDING_SENT",
                    modifier = Modifier.weight(1f)
                )

                SecondaryButton(
                    text = "Profile",
                    onClick = onCardClick,
                    modifier = Modifier.width(90.dp)
                )
            }
        }
    }
}
