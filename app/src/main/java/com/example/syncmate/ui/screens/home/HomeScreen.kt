package com.example.syncmate.ui.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Checklist
import androidx.compose.material.icons.filled.CompareArrows
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.HelpCenter
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PrivacyTip
import androidx.compose.material.icons.filled.RateReview
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.syncmate.data.model.HousingListing
import com.example.syncmate.util.CurrencyFormatter
import com.example.syncmate.ui.components.AnimatedSaveButton
import com.example.syncmate.ui.components.AutoFeatureCarousel
import com.example.syncmate.ui.components.GlassCard
import com.example.syncmate.ui.components.LoadingIndicator
import com.example.syncmate.ui.components.StatBadge
import com.example.syncmate.ui.theme.EmeraldSuccess
import com.example.syncmate.ui.theme.IndigoPrimary
import com.example.syncmate.ui.theme.SlateBackground
import com.example.syncmate.ui.theme.SlateCard
import com.example.syncmate.ui.theme.SlateCardBorder
import com.example.syncmate.ui.theme.TextPrimary
import com.example.syncmate.ui.theme.TextSecondary
import com.example.syncmate.viewmodel.HomeViewModel

@Composable
fun HomeScreen(
    homeViewModel: HomeViewModel,
    onNavigateToSearch: () -> Unit,
    onNavigateToFilters: () -> Unit,
    onNavigateToListingDetails: (String) -> Unit,
    onNavigateToFairRent: () -> Unit,
    onNavigateToUtilityPredictor: () -> Unit,
    onNavigateToCompareListings: () -> Unit,
    onNavigateToFakeListing: () -> Unit,
    onNavigateToUploadPhotos: () -> Unit,
    onNavigateToContractGuard: () -> Unit,
    onNavigateToSavedListings: () -> Unit,
    onNavigateToChat: () -> Unit,
    onNavigateToNotifications: () -> Unit,
    onNavigateToProfile: () -> Unit,
    onNavigateToLeaseChecklist: () -> Unit,
    onNavigateToPaymentSafety: () -> Unit,
    onNavigateToHelpCenter: () -> Unit,
    onNavigateToSettings: () -> Unit,
    onNavigateToPrivacy: () -> Unit,
    onNavigateToFeedback: () -> Unit,
    onNavigateToPreferences: () -> Unit
) {
    val uiState by homeViewModel.uiState.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SlateBackground)
            .safeDrawingPadding()
    ) {
        // Top Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "SyncMate",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Text(
                    text = "Campus Housing & AI Tools",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary
                )
            }

            Row {
                IconButton(onClick = onNavigateToNotifications) {
                    Icon(imageVector = Icons.Default.Notifications, contentDescription = "Notifications", tint = TextPrimary)
                }
                IconButton(onClick = onNavigateToProfile) {
                    Icon(imageVector = Icons.Default.Person, contentDescription = "Profile", tint = TextPrimary)
                }
            }
        }

        if (uiState.isLoading) {
            LoadingIndicator(message = "Fetching verified student housing...")
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(bottom = 24.dp)
            ) {
                // Search Bar Trigger
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(SlateCard)
                        .border(1.dp, SlateCardBorder, RoundedCornerShape(14.dp))
                        .clickable { onNavigateToSearch() }
                        .padding(horizontal = 16.dp, vertical = 14.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(imageVector = Icons.Default.Search, contentDescription = "Search", tint = TextSecondary)
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = "Search by university, address, price...",
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextSecondary,
                            modifier = Modifier.weight(1f)
                        )
                        IconButton(onClick = onNavigateToFilters) {
                            Icon(imageVector = Icons.Default.FilterList, contentDescription = "Filters", tint = IndigoPrimary)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Auto-Sliding Feature Showcase Banner
                AutoFeatureCarousel(
                    onNavigateToFairRent = onNavigateToFairRent,
                    onNavigateToFakeListing = onNavigateToFakeListing,
                    onNavigateToContractGuard = onNavigateToContractGuard,
                    onNavigateToPreferences = onNavigateToPreferences
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Core AI Tools Section
                Text(
                    text = "AI Smart Protection Tools",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary,
                    modifier = Modifier.padding(horizontal = 20.dp)
                )

                Spacer(modifier = Modifier.height(12.dp))

                LazyRow(
                    contentPadding = PaddingValues(horizontal = 20.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    item {
                        AiFeatureChip(
                            title = "AI Fair-Rent",
                            subtitle = "ML Rent Estimator",
                            icon = Icons.Default.Analytics,
                            onClick = onNavigateToFairRent
                        )
                    }
                    item {
                        AiFeatureChip(
                            title = "Fake Shield",
                            subtitle = "Scam Detector",
                            icon = Icons.Default.Security,
                            onClick = onNavigateToFakeListing
                        )
                    }
                    item {
                        AiFeatureChip(
                            title = "Contract Guard",
                            subtitle = "Legal Lease Review",
                            icon = Icons.Default.Description,
                            onClick = onNavigateToContractGuard
                        )
                    }
                    item {
                        AiFeatureChip(
                            title = "Utility Predictor",
                            subtitle = "Cost Estimator",
                            icon = Icons.Default.Analytics,
                            onClick = onNavigateToUtilityPredictor
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Featured Sublets Header
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Verified Student Sublets",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Text(
                        text = "See All",
                        style = MaterialTheme.typography.labelLarge,
                        color = IndigoPrimary,
                        modifier = Modifier.clickable { onNavigateToSearch() }
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Column(
                    modifier = Modifier.padding(horizontal = 20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    uiState.featuredListings.forEach { listing ->
                        ListingCardItem(
                            listing = listing,
                            onClick = { onNavigateToListingDetails(listing.id) },
                            onToggleSave = { homeViewModel.toggleSaveListing(listing.id) }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(28.dp))

                // Additional Navigation Shortcuts Matrix
                Text(
                    text = "All SyncMate Features",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary,
                    modifier = Modifier.padding(horizontal = 20.dp)
                )

                Spacer(modifier = Modifier.height(12.dp))

                Column(
                    modifier = Modifier.padding(horizontal = 20.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        FeatureNavTile(
                            title = "Saved",
                            icon = Icons.Default.Bookmark,
                            onClick = onNavigateToSavedListings,
                            modifier = Modifier.weight(1f)
                        )
                        FeatureNavTile(
                            title = "Compare",
                            icon = Icons.Default.CompareArrows,
                            onClick = onNavigateToCompareListings,
                            modifier = Modifier.weight(1f)
                        )
                        FeatureNavTile(
                            title = "Post Sublet",
                            icon = Icons.Default.Add,
                            onClick = onNavigateToUploadPhotos,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        FeatureNavTile(
                            title = "Messages",
                            icon = Icons.Default.Chat,
                            onClick = onNavigateToChat,
                            modifier = Modifier.weight(1f)
                        )
                        FeatureNavTile(
                            title = "Lease Checklist",
                            icon = Icons.Default.Checklist,
                            onClick = onNavigateToLeaseChecklist,
                            modifier = Modifier.weight(1f)
                        )
                        FeatureNavTile(
                            title = "Pay Safety",
                            icon = Icons.Default.Security,
                            onClick = onNavigateToPaymentSafety,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        FeatureNavTile(
                            title = "Help Center",
                            icon = Icons.Default.HelpCenter,
                            onClick = onNavigateToHelpCenter,
                            modifier = Modifier.weight(1f)
                        )
                        FeatureNavTile(
                            title = "Settings",
                            icon = Icons.Default.Settings,
                            onClick = onNavigateToSettings,
                            modifier = Modifier.weight(1f)
                        )
                        FeatureNavTile(
                            title = "Feedback",
                            icon = Icons.Default.RateReview,
                            onClick = onNavigateToFeedback,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        FeatureNavTile(
                            title = "Preferences",
                            icon = Icons.Default.Person,
                            onClick = onNavigateToPreferences,
                            modifier = Modifier.weight(1f)
                        )
                        FeatureNavTile(
                            title = "Privacy",
                            icon = Icons.Default.PrivacyTip,
                            onClick = onNavigateToPrivacy,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun AiFeatureChip(
    title: String,
    subtitle: String,
    icon: ImageVector,
    onClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = SlateCard),
        modifier = Modifier
            .width(160.dp)
            .clickable { onClick() }
            .border(1.dp, SlateCardBorder, RoundedCornerShape(16.dp))
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(IndigoPrimary.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(imageVector = icon, contentDescription = title, tint = IndigoPrimary)
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.labelSmall,
                color = TextSecondary
            )
        }
    }
}

@Composable
fun ListingCardItem(
    listing: HousingListing,
    onClick: () -> Unit,
    onToggleSave: () -> Unit
) {
    GlassCard(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                StatBadge(
                    text = "VERIFIED STUDENT",
                    backgroundColor = EmeraldSuccess
                )

                Box(
                    modifier = Modifier.clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) {
                        onToggleSave()
                    }
                ) {
                    AnimatedSaveButton(
                        isSaved = listing.isSaved,
                        onToggle = onToggleSave
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = listing.title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(top = 4.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = null,
                    tint = TextSecondary,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "${listing.address} • ${listing.distanceToCampus}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = CurrencyFormatter.formatMonthlyRent(listing.priceMonthly),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = IndigoPrimary
                )

                Text(
                    text = "${listing.roomType} • ${listing.duration}",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextSecondary
                )
            }
        }
    }
}

@Composable
fun FeatureNavTile(
    title: String,
    icon: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(SlateCard)
            .border(1.dp, SlateCardBorder, RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .padding(vertical = 12.dp, horizontal = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = IndigoPrimary,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.labelSmall,
                color = TextPrimary,
                fontWeight = FontWeight.Medium
            )
        }
    }
}
