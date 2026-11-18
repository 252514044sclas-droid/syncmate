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
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.syncmate.data.model.RoommateProfile
import com.example.syncmate.ui.components.AnimatedSaveButton
import com.example.syncmate.ui.components.AppHeader
import com.example.syncmate.ui.components.GlassCard
import com.example.syncmate.ui.components.PrimaryButton
import com.example.syncmate.ui.components.SecondaryButton
import com.example.syncmate.ui.components.StatBadge
import com.example.syncmate.ui.theme.EmeraldSuccess
import com.example.syncmate.ui.theme.IndigoPrimary
import com.example.syncmate.ui.theme.SlateBackground
import com.example.syncmate.ui.theme.SlateCard
import com.example.syncmate.ui.theme.SlateCardBorder
import com.example.syncmate.ui.theme.TextPrimary
import com.example.syncmate.ui.theme.TextSecondary
import com.example.syncmate.util.CurrencyFormatter

@Composable
fun RoommatesScreen(
    onNavigateToChat: () -> Unit,
    onNavigateToQuiz: () -> Unit,
    onNavigateBack: () -> Unit
) {
    val haptic = LocalHapticFeedback.current
    var selectedFilter by remember { mutableStateOf("All Matches") }
    var selectedRoommateIds by remember { mutableStateOf(setOf<String>()) }

    val sampleRoommates = remember {
        listOf(
            RoommateProfile(
                id = "rm_1",
                name = "Maya Lin",
                university = "Anna University '26",
                compatibilityPercentage = 96,
                matchReason = "Matches your quiet study hours & budget",
                lifestyleRhythm = "Early Riser • Non-Smoker • Quiet Study",
                maxBudgetMonthly = 12000.0,
                preferredLocation = "Adyar / Guindy",
                bio = "CS junior looking for a clean, peaceful sublet room for summer internship."
            ),
            RoommateProfile(
                id = "rm_2",
                name = "Jordan Patel",
                university = "IIT Madras '25",
                compatibilityPercentage = 91,
                matchReason = "Similar lease term & campus quadrant",
                lifestyleRhythm = "Night Owl • Quiet & Organized",
                maxBudgetMonthly = 16500.0,
                preferredLocation = "Koramangala, Bengaluru",
                bio = "Grad student in ECE. Friendly, respectful, and loves cooking on weekends."
            ),
            RoommateProfile(
                id = "rm_3",
                name = "Chloe Bennett",
                university = "Delhi University '25",
                compatibilityPercentage = 88,
                matchReason = "Shared campus commute & budget preference",
                lifestyleRhythm = "Flexible • Social & Clean",
                maxBudgetMonthly = 14000.0,
                preferredLocation = "North Campus, Delhi",
                bio = "Commerce major looking for a sublet roommate near North Campus."
            ),
            RoommateProfile(
                id = "rm_4",
                name = "Rohan Sharma",
                university = "IIT Bombay '26",
                compatibilityPercentage = 85,
                matchReason = "Loves quiet study hours & gym rhythm",
                lifestyleRhythm = "Early Riser • Non-Smoker • Gym",
                maxBudgetMonthly = 18000.0,
                preferredLocation = "Powai, Mumbai",
                bio = "Mechanical Engineering senior looking for a clean studio or 2BHK roommate near IIT gate."
            ),
            RoommateProfile(
                id = "rm_5",
                name = "Priya Nair",
                university = "Symbiosis Pune '26",
                compatibilityPercentage = 93,
                matchReason = "Matches budget & non-smoking preference",
                lifestyleRhythm = "Quiet Study • Non-Smoker • Veg",
                maxBudgetMonthly = 13500.0,
                preferredLocation = "Viman Nagar, Pune",
                bio = "MBA student looking for a friendly roommate to share 2BHK sublet near Symbiosis."
            )
        )
    }

    val filterOptions = listOf("All Matches", "90%+ Synergy", "Early Riser", "Quiet Study")

    val filteredList = remember(selectedFilter, sampleRoommates) {
        when (selectedFilter) {
            "90%+ Synergy" -> sampleRoommates.filter { it.compatibilityPercentage >= 90 }
            "Early Riser" -> sampleRoommates.filter { it.lifestyleRhythm.contains("Early Riser", ignoreCase = true) }
            "Quiet Study" -> sampleRoommates.filter { it.lifestyleRhythm.contains("Quiet", ignoreCase = true) }
            else -> sampleRoommates
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SlateBackground)
            .safeDrawingPadding()
    ) {
        AppHeader(
            title = "AI Compatible Roommates",
            onBackClick = onNavigateBack
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp)
        ) {
            Text(
                text = "Select Student Roommates",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )

            Text(
                text = "Matched using your 10-question Onboarding Quiz preferences and Neural Network AI.",
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary,
                modifier = Modifier.padding(top = 4.dp, bottom = 16.dp)
            )

            // Filter Chips
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(bottom = 16.dp)
            ) {
                items(filterOptions) { filter ->
                    val isSelected = filter == selectedFilter
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
                                selectedFilter = filter
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

            LazyColumn(
                contentPadding = PaddingValues(bottom = 24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.weight(1f)
            ) {
                items(filteredList, key = { it.id }) { roommate ->
                    val isSelectedRoommate = selectedRoommateIds.contains(roommate.id)

                    GlassCard(
                        modifier = Modifier.fillMaxWidth()
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
                                            .size(48.dp)
                                            .clip(CircleShape)
                                            .background(IndigoPrimary.copy(alpha = 0.2f)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Person,
                                            contentDescription = roommate.name,
                                            tint = IndigoPrimary,
                                            modifier = Modifier.size(28.dp)
                                        )
                                    }

                                    Spacer(modifier = Modifier.width(12.dp))

                                    Column {
                                        Text(
                                            text = roommate.name,
                                            style = MaterialTheme.typography.titleLarge,
                                            fontWeight = FontWeight.Bold,
                                            color = TextPrimary
                                        )
                                        Text(
                                            text = roommate.university,
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = TextSecondary
                                        )
                                    }
                                }

                                StatBadge(
                                    text = "${roommate.compatibilityPercentage}% MATCH",
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

                            Spacer(modifier = Modifier.height(6.dp))

                            Text(
                                text = roommate.bio,
                                style = MaterialTheme.typography.bodyMedium,
                                color = TextPrimary
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Budget: ${CurrencyFormatter.formatMonthlyRent(roommate.maxBudgetMonthly)}",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = IndigoPrimary
                                )

                                Text(
                                    text = "Area: ${roommate.preferredLocation}",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = TextSecondary
                                )
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                PrimaryButton(
                                    text = if (isSelectedRoommate) "✓ Roommate Selected" else "Select as Roommate",
                                    onClick = {
                                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                        selectedRoommateIds = if (isSelectedRoommate) {
                                            selectedRoommateIds - roommate.id
                                        } else {
                                            selectedRoommateIds + roommate.id
                                        }
                                    },
                                    modifier = Modifier.weight(1f)
                                )

                                SecondaryButton(
                                    text = "Chat",
                                    onClick = onNavigateToChat,
                                    modifier = Modifier.width(90.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
