package com.example.syncmate.ui.screens.dashboard

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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.HomeWork
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.syncmate.data.model.HousingListing
import com.example.syncmate.data.model.RoommateProfile
import com.example.syncmate.ui.components.AnimatedSaveButton
import com.example.syncmate.ui.components.GlassCard
import com.example.syncmate.ui.components.LoadingIndicator
import com.example.syncmate.ui.components.PrimaryButton
import com.example.syncmate.ui.components.StatBadge
import com.example.syncmate.ui.theme.EmeraldSuccess
import com.example.syncmate.ui.theme.IndigoPrimary
import com.example.syncmate.ui.theme.SlateBackground
import com.example.syncmate.ui.theme.SlateCard
import com.example.syncmate.ui.theme.SlateCardBorder
import com.example.syncmate.ui.theme.TextPrimary
import com.example.syncmate.ui.theme.TextSecondary
import com.example.syncmate.viewmodel.DashboardViewModel

@Composable
fun DashboardScreen(
    dashboardViewModel: DashboardViewModel,
    onNavigateToSearch: () -> Unit,
    onNavigateToFilters: () -> Unit,
    onNavigateToListingDetails: (String) -> Unit,
    onNavigateToFairRent: () -> Unit,
    onNavigateToContractGuard: () -> Unit,
    onNavigateToSavedListings: () -> Unit,
    onNavigateToChat: () -> Unit,
    onNavigateToNotifications: () -> Unit,
    onNavigateToProfile: () -> Unit,
    onNavigateToPreferences: () -> Unit,
    onNavigateToSafety: () -> Unit
) {
    val uiState by dashboardViewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        bottomBar = {
            DashboardBottomNavigation(
                selectedItem = 0,
                unreadMessages = uiState.unreadMessageCount,
                onNavigateToHome = { },
                onNavigateToExplore = onNavigateToSearch,
                onNavigateToSaved = onNavigateToSavedListings,
                onNavigateToMessages = onNavigateToChat,
                onNavigateToProfile = onNavigateToProfile
            )
        },
        containerColor = SlateBackground
    ) { innerPadding ->
        if (uiState.isLoading) {
            LoadingIndicator(
                message = "Loading SubletSync Student Dashboard...",
                modifier = Modifier.padding(innerPadding)
            )
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .verticalScroll(rememberScrollState())
                    .padding(bottom = 16.dp)
            ) {
                // 1. Top Bar
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(IndigoPrimary),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.HomeWork,
                                contentDescription = "SubletSync Logo",
                                tint = Color.White,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "SubletSync",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(onClick = onNavigateToNotifications) {
                            BadgedBox(
                                badge = {
                                    if (uiState.unreadNotificationCount > 0) {
                                        Badge(
                                            containerColor = IndigoPrimary,
                                            contentColor = Color.White
                                        ) {
                                            Text(text = "${uiState.unreadNotificationCount}")
                                        }
                                    }
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Notifications,
                                    contentDescription = "Notifications",
                                    tint = TextPrimary
                                )
                            }
                        }

                        IconButton(onClick = onNavigateToProfile) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(IndigoPrimary.copy(alpha = 0.2f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Person,
                                    contentDescription = "Profile",
                                    tint = IndigoPrimary,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                        }
                    }
                }

                // 2. Welcome Greeting Section
                Column(
                    modifier = Modifier.padding(horizontal = 20.dp)
                ) {
                    Text(
                        text = "Welcome back, ${uiState.userName}! 👋",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Text(
                        text = "Find a verified student sublet or roommate that fits your rhythm.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondary,
                        modifier = Modifier.padding(top = 4.dp, bottom = 16.dp)
                    )
                }

                // 3. Search Housing Bar
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
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.Search, contentDescription = "Search", tint = TextSecondary)
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = "Search by university, area, or budget...",
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextSecondary,
                            modifier = Modifier.weight(1f)
                        )
                        IconButton(onClick = onNavigateToFilters) {
                            Icon(imageVector = Icons.Default.FilterList, contentDescription = "Filters", tint = IndigoPrimary)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // 4. Quick Actions Section
                Text(
                    text = "Quick Actions",
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
                        QuickActionGridCard(
                            title = "Find Housing",
                            subtitle = "Explore Sublets",
                            icon = Icons.Default.HomeWork,
                            onClick = onNavigateToSearch,
                            modifier = Modifier.weight(1f)
                        )
                        QuickActionGridCard(
                            title = "Roommates",
                            subtitle = "Match Rhythm",
                            icon = Icons.Default.People,
                            onClick = onNavigateToPreferences,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        QuickActionGridCard(
                            title = "Fair Rent",
                            subtitle = "ML Estimator",
                            icon = Icons.Default.Analytics,
                            onClick = onNavigateToFairRent,
                            modifier = Modifier.weight(1f)
                        )
                        QuickActionGridCard(
                            title = "Contract Guard",
                            subtitle = "Lease Legal Scan",
                            icon = Icons.Default.Description,
                            onClick = onNavigateToContractGuard,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(28.dp))

                // 5. Recommended Housing Section
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Recommended Sublets",
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

                LazyRow(
                    contentPadding = PaddingValues(horizontal = 20.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(uiState.recommendedListings) { listing ->
                        DashboardHousingCard(
                            listing = listing,
                            onClick = { onNavigateToListingDetails(listing.id) },
                            onToggleSave = { dashboardViewModel.toggleSaveListing(listing.id) }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(28.dp))

                // 6. Roommate Matches Section
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "AI Roommate Matches",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Text(
                        text = "Preferences",
                        style = MaterialTheme.typography.labelLarge,
                        color = IndigoPrimary,
                        modifier = Modifier.clickable { onNavigateToPreferences() }
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                LazyRow(
                    contentPadding = PaddingValues(horizontal = 20.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(uiState.roommateMatches) { roommate ->
                        DashboardRoommateCard(
                            roommate = roommate,
                            onClick = onNavigateToChat,
                            onToggleSave = { dashboardViewModel.toggleFavoriteRoommate(roommate.id) }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(28.dp))

                // 7. AI Safety Feature Banner Card
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = SlateCard),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp)
                        .border(1.dp, SlateCardBorder, RoundedCornerShape(16.dp))
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(44.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(IndigoPrimary.copy(alpha = 0.2f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Security,
                                    contentDescription = "AI Safety",
                                    tint = IndigoPrimary,
                                    modifier = Modifier.size(26.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "AI Sublet Protection & Scam Shield",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )
                                Text(
                                    text = "Detect payment fraud and verify student leases",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = TextSecondary
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = "Every sublet on SubletSync passes automated scam checks. Avoid wire transfer fraud and legal lease traps before signing.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextSecondary
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        PrimaryButton(
                            text = "Explore AI Safety Guidelines",
                            onClick = onNavigateToSafety
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun QuickActionGridCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = SlateCard),
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .clickable { onClick() }
            .border(1.dp, SlateCardBorder, RoundedCornerShape(14.dp))
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(IndigoPrimary.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(imageVector = icon, contentDescription = title, tint = IndigoPrimary, modifier = Modifier.size(22.dp))
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column {
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
}

@Composable
fun DashboardHousingCard(
    listing: HousingListing,
    onClick: () -> Unit,
    onToggleSave: () -> Unit
) {
    GlassCard(
        onClick = onClick,
        modifier = Modifier.width(260.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                StatBadge(text = "VERIFIED STUDENT", backgroundColor = EmeraldSuccess)
                AnimatedSaveButton(
                    isSaved = listing.isSaved,
                    onToggle = onToggleSave
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = listing.title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = TextPrimary,
                maxLines = 1
            )

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(top = 4.dp)
            ) {
                Icon(imageVector = Icons.Default.LocationOn, contentDescription = null, tint = TextSecondary, modifier = Modifier.size(14.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = listing.distanceToCampus,
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
                    text = "$${listing.priceMonthly.toInt()}/mo",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = IndigoPrimary
                )
                Text(
                    text = listing.roomType,
                    style = MaterialTheme.typography.labelSmall,
                    color = TextSecondary
                )
            }
        }
    }
}

@Composable
fun DashboardRoommateCard(
    roommate: RoommateProfile,
    onClick: () -> Unit,
    onToggleSave: () -> Unit
) {
    GlassCard(
        onClick = onClick,
        modifier = Modifier.width(240.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                StatBadge(
                    text = "${roommate.compatibilityPercentage}% MATCH",
                    backgroundColor = IndigoPrimary
                )
                AnimatedSaveButton(
                    isSaved = roommate.isSaved,
                    onToggle = onToggleSave
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = roommate.name,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )

            Text(
                text = roommate.university,
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = roommate.lifestyleRhythm,
                style = MaterialTheme.typography.labelSmall,
                color = TextPrimary,
                fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "Budget: $${roommate.maxBudgetMonthly.toInt()}/mo",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = IndigoPrimary
            )
        }
    }
}

@Composable
fun DashboardBottomNavigation(
    selectedItem: Int,
    unreadMessages: Int,
    onNavigateToHome: () -> Unit,
    onNavigateToExplore: () -> Unit,
    onNavigateToSaved: () -> Unit,
    onNavigateToMessages: () -> Unit,
    onNavigateToProfile: () -> Unit
) {
    NavigationBar(
        containerColor = SlateCard,
        contentColor = TextPrimary
    ) {
        NavigationBarItem(
            selected = selectedItem == 0,
            onClick = onNavigateToHome,
            icon = { Icon(imageVector = Icons.Default.Home, contentDescription = "Home") },
            label = { Text("Home") },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = IndigoPrimary,
                selectedTextColor = IndigoPrimary,
                unselectedIconColor = TextSecondary,
                unselectedTextColor = TextSecondary,
                indicatorColor = IndigoPrimary.copy(alpha = 0.15f)
            )
        )

        NavigationBarItem(
            selected = selectedItem == 1,
            onClick = onNavigateToExplore,
            icon = { Icon(imageVector = Icons.Default.Explore, contentDescription = "Explore") },
            label = { Text("Explore") },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = IndigoPrimary,
                selectedTextColor = IndigoPrimary,
                unselectedIconColor = TextSecondary,
                unselectedTextColor = TextSecondary,
                indicatorColor = IndigoPrimary.copy(alpha = 0.15f)
            )
        )

        NavigationBarItem(
            selected = selectedItem == 2,
            onClick = onNavigateToSaved,
            icon = { Icon(imageVector = Icons.Default.Bookmark, contentDescription = "Saved") },
            label = { Text("Saved") },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = IndigoPrimary,
                selectedTextColor = IndigoPrimary,
                unselectedIconColor = TextSecondary,
                unselectedTextColor = TextSecondary,
                indicatorColor = IndigoPrimary.copy(alpha = 0.15f)
            )
        )

        NavigationBarItem(
            selected = selectedItem == 3,
            onClick = onNavigateToMessages,
            icon = {
                BadgedBox(
                    badge = {
                        if (unreadMessages > 0) {
                            Badge(containerColor = IndigoPrimary, contentColor = Color.White) {
                                Text("$unreadMessages")
                            }
                        }
                    }
                ) {
                    Icon(imageVector = Icons.AutoMirrored.Filled.Chat, contentDescription = "Messages")
                }
            },
            label = { Text("Messages") },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = IndigoPrimary,
                selectedTextColor = IndigoPrimary,
                unselectedIconColor = TextSecondary,
                unselectedTextColor = TextSecondary,
                indicatorColor = IndigoPrimary.copy(alpha = 0.15f)
            )
        )

        NavigationBarItem(
            selected = selectedItem == 4,
            onClick = onNavigateToProfile,
            icon = { Icon(imageVector = Icons.Default.Person, contentDescription = "Profile") },
            label = { Text("Profile") },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = IndigoPrimary,
                selectedTextColor = IndigoPrimary,
                unselectedIconColor = TextSecondary,
                unselectedTextColor = TextSecondary,
                indicatorColor = IndigoPrimary.copy(alpha = 0.15f)
            )
        )
    }
}
