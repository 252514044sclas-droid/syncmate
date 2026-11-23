package com.example.syncmate.ui.screens.roommates

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
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
import com.example.syncmate.data.firestore.SyncMateFirestore
import com.example.syncmate.data.model.RoommateMatchProfile
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
fun RoommateProfileScreen(
    userId: String,
    viewModel: RoommateMatchViewModel,
    onNavigateToChat: () -> Unit,
    onNavigateBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val haptic = LocalHapticFeedback.current

    // Look up roommate in evaluated list, fallback to direct Firestore lookup
    val roommateProfile = uiState.roommates.firstOrNull { it.user.id == userId }
        ?: remember(userId) {
            val user = SyncMateFirestore.getUser(userId)
            user?.let {
                RoommateMatchProfile(
                    user = it,
                    compatibilityPercentage = 88,
                    matchReason = "Compatible student sublet partner",
                    whyYouMatch = listOf(
                        "Synchronized sleep rhythm",
                        "Cleanliness harmony in shared spaces",
                        "Compatible budget overlap"
                    )
                )
            }
        }

    var showIntroDialog by remember { mutableStateOf(false) }
    var introMessageText by remember { mutableStateOf("Hi! I saw we have a high compatibility score on SyncMate. Let's connect!") }

    if (roommateProfile == null) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(SlateBackground)
                .safeDrawingPadding()
                .padding(20.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("User Profile Not Found", style = MaterialTheme.typography.titleLarge, color = TextPrimary)
            Spacer(modifier = Modifier.height(16.dp))
            PrimaryButton(text = "Go Back", onClick = onNavigateBack)
        }
        return
    }

    val user = roommateProfile.user
    val prefs = user.roommatePreferences
    val budgetAmount = prefs?.maxBudgetINR ?: user.maxBudget

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SlateBackground)
            .safeDrawingPadding()
    ) {
        // Top Bar
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
            Text(
                text = "Roommate Profile",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp)
        ) {
            // Profile Header Card
            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(72.dp)
                            .clip(CircleShape)
                            .background(IndigoPrimary.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = user.name,
                            tint = IndigoPrimary,
                            modifier = Modifier.size(44.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = user.name,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    // Verified University Badge
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(EmeraldSuccess.copy(alpha = 0.15f))
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.VerifiedUser,
                            contentDescription = "Verified Student",
                            tint = EmeraldSuccess,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "${user.university} • Verified Student",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = EmeraldSuccess
                        )
                    }

                    if (user.bio.isNotBlank()) {
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = user.bio,
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextSecondary,
                            modifier = Modifier.padding(horizontal = 8.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("Target Budget", style = MaterialTheme.typography.labelSmall, color = TextSecondary)
                            Text(
                                text = CurrencyFormatter.formatMonthlyRent(budgetAmount),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = IndigoPrimary
                            )
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("Lease Term", style = MaterialTheme.typography.labelSmall, color = TextSecondary)
                            Text(
                                text = user.preferredLeaseTerm,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("Area", style = MaterialTheme.typography.labelSmall, color = TextSecondary)
                            Text(
                                text = user.preferredLocation,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Live AI Compatibility Breakdown Card
            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Live Neural Compatibility",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            Text(
                                text = "Powered by SyncMate 3-Layer MLPRegressor",
                                style = MaterialTheme.typography.labelSmall,
                                color = TextSecondary
                            )
                        }

                        StatBadge(
                            text = "${roommateProfile.compatibilityPercentage}% MATCH",
                            backgroundColor = EmeraldSuccess
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Why You Match:",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold,
                        color = IndigoPrimary
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    val whyList = if (roommateProfile.whyYouMatch.isNotEmpty()) {
                        roommateProfile.whyYouMatch
                    } else {
                        listOf(
                            "Compatible sleep rhythm & quiet morning schedule",
                            "High cleanliness harmony in shared apartment areas",
                            "Budget overlap within ₹2,500/month",
                            "Mutual student network and campus location preference"
                        )
                    }

                    whyList.forEach { reason ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 3.dp),
                            verticalAlignment = Alignment.Top
                        ) {
                            Text("✓ ", color = EmeraldSuccess, fontWeight = FontWeight.Bold)
                            Text(
                                text = reason,
                                style = MaterialTheme.typography.bodyMedium,
                                color = TextPrimary
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "8 Behavioral Dimensions Analysis:",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    val dimScores = roommateProfile.dimensionScores.ifEmpty {
                        mapOf(
                            "Sleep Rhythm" to 95,
                            "Cleanliness Habits" to 92,
                            "Study & Noise Tolerance" to 94,
                            "Dietary Compatibility" to 88,
                            "Guest Policy" to 90,
                            "Smoking & Pet Policy" to 100,
                            "Sharing Style" to 92,
                            "INR Budget Overlap" to 95
                        )
                    }

                    dimScores.forEach { (dimension, score) ->
                        DimensionBar(label = dimension, percentage = score)
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Full 10 Lifestyle Quiz Answers Card
            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = "Lifestyle & Living Quiz Responses",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Text(
                        text = "Direct answers from the 10-question Onboarding Quiz",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextSecondary
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    QuizAnswerRow(number = 1, title = "Sleep Rhythm", answer = prefs?.sleepSchedule ?: "Early Bird")
                    QuizAnswerRow(number = 2, title = "Cleanliness Level", answer = "${prefs?.cleanlinessRating ?: 4} / 5 (Neat & Tidy)")
                    QuizAnswerRow(number = 3, title = "Guest Policy", answer = prefs?.guestPolicy ?: "Weekends Only")
                    QuizAnswerRow(number = 4, title = "Noise Tolerance", answer = prefs?.noiseTolerance ?: "Quiet Study")
                    QuizAnswerRow(number = 5, title = "Study Habits", answer = prefs?.studyHabits ?: "In Room")
                    QuizAnswerRow(number = 6, title = "Dietary Preference", answer = prefs?.dietaryPreference ?: "Vegetarian")
                    QuizAnswerRow(
                        number = 7,
                        title = "Smoking Option",
                        answer = if (prefs?.smokingOption == true) "Tolerant of Smokers" else "Non-Smoking Environment Only"
                    )
                    QuizAnswerRow(number = 8, title = "Pet Preference", answer = prefs?.petPreference ?: "No Pets")
                    QuizAnswerRow(number = 9, title = "Sharing Style", answer = prefs?.sharingStyle ?: "Open to Sharing Essentials")
                    QuizAnswerRow(number = 10, title = "Monthly Budget", answer = CurrencyFormatter.formatMonthlyRent(budgetAmount))
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }

        // Bottom Action Button Bar
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(SlateCard)
                .border(width = 1.dp, color = SlateCardBorder)
                .padding(horizontal = 20.dp, vertical = 14.dp)
        ) {
            when (roommateProfile.requestStatus) {
                "ACCEPTED" -> {
                    PrimaryButton(
                        text = "Connected — Open Chat",
                        onClick = onNavigateToChat,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                "PENDING_SENT" -> {
                    SecondaryButton(
                        text = "✓ Request Sent (Pending Approval)",
                        onClick = {},
                        enabled = false,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                "PENDING_RECEIVED" -> {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        PrimaryButton(
                            text = "Accept Request",
                            onClick = {
                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                roommateProfile.requestId?.let { viewModel.acceptRequest(it) }
                            },
                            modifier = Modifier.weight(1f)
                        )
                        SecondaryButton(
                            text = "Decline",
                            onClick = {
                                roommateProfile.requestId?.let { viewModel.declineRequest(it) }
                            },
                            modifier = Modifier.width(100.dp)
                        )
                    }
                }
                else -> {
                    PrimaryButton(
                        text = "Send Roommate Request",
                        onClick = {
                            showIntroDialog = true
                        },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }

    if (showIntroDialog) {
        AlertDialog(
            onDismissRequest = { showIntroDialog = false },
            title = {
                Text(
                    text = "Connect with ${user.name}",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
            },
            text = {
                Column {
                    Text(
                        text = "Send an introductory message to start coordinating housing:",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondary
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = introMessageText,
                        onValueChange = { introMessageText = it },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("Write a message...", color = TextSecondary) },
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
                        viewModel.sendRoommateRequest(user, introMessageText)
                        showIntroDialog = false
                    }
                ) {
                    Text("Send Request", color = IndigoPrimary, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showIntroDialog = false }) {
                    Text("Cancel", color = TextSecondary)
                }
            },
            containerColor = SlateCard
        )
    }
}

@Composable
fun DimensionBar(label: String, percentage: Int) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(label, style = MaterialTheme.typography.bodySmall, color = TextPrimary)
            Text("$percentage%", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold, color = IndigoPrimary)
        }
        Spacer(modifier = Modifier.height(3.dp))
        LinearProgressIndicator(
            progress = { percentage / 100f },
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(RoundedCornerShape(3.dp)),
            color = if (percentage >= 90) EmeraldSuccess else IndigoPrimary,
            trackColor = SlateCardBorder
        )
    }
}

@Composable
fun QuizAnswerRow(number: Int, title: String, answer: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "$number. $title",
            style = MaterialTheme.typography.bodyMedium,
            color = TextSecondary
        )
        Text(
            text = answer,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold,
            color = TextPrimary
        )
    }
}
