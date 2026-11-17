package com.example.syncmate.ui.screens.search

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.syncmate.ui.components.AppHeader
import com.example.syncmate.ui.components.AppTextField
import com.example.syncmate.ui.components.EmptyStateView
import com.example.syncmate.ui.screens.home.ListingCardItem
import com.example.syncmate.ui.theme.IndigoPrimary
import com.example.syncmate.ui.theme.SlateBackground
import com.example.syncmate.ui.theme.TextSecondary
import com.example.syncmate.viewmodel.HousingViewModel

@Composable
fun SearchScreen(
    housingViewModel: HousingViewModel,
    onNavigateToListingDetails: (String) -> Unit,
    onNavigateToFilters: () -> Unit,
    onNavigateBack: () -> Unit
) {
    val uiState by housingViewModel.uiState.collectAsStateWithLifecycle()
    var searchQuery by remember { mutableStateOf("") }

    // Multi-criteria real-time search filter
    val filteredListings = remember(searchQuery, uiState.listings) {
        if (searchQuery.isBlank()) {
            uiState.listings
        } else {
            val query = searchQuery.trim().lowercase()
            val parsedPrice = query.filter { it.isDigit() }.toDoubleOrNull()

            uiState.listings.filter { listing ->
                val titleMatch = listing.title.lowercase().contains(query)
                val addressMatch = listing.address.lowercase().contains(query)
                val roomTypeMatch = listing.roomType.lowercase().contains(query)
                val descriptionMatch = listing.description.lowercase().contains(query)
                val ownerMatch = listing.ownerName.lowercase().contains(query)
                val distanceMatch = listing.distanceToCampus.lowercase().contains(query)

                val priceMatch = if (parsedPrice != null && parsedPrice > 1000) {
                    listing.priceMonthly <= parsedPrice || listing.priceMonthly.toInt().toString().contains(query)
                } else {
                    false
                }

                titleMatch || addressMatch || roomTypeMatch || descriptionMatch || ownerMatch || distanceMatch || priceMatch
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
            title = "Search Sublets",
            onBackClick = onNavigateBack,
            action = {
                IconButton(onClick = onNavigateToFilters) {
                    Icon(imageVector = Icons.Default.FilterList, contentDescription = "Filters", tint = IndigoPrimary)
                }
            }
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .imePadding()
                .padding(horizontal = 20.dp)
        ) {
            AppTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                label = "Search",
                placeholder = "Search Chennai, Bengaluru, Pune, Delhi, price...",
                leadingIcon = {
                    Icon(imageVector = Icons.Default.Search, contentDescription = null, tint = TextSecondary)
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = if (searchQuery.isBlank()) "Found ${filteredListings.size} verified sublets" else "Found ${filteredListings.size} sublets matching '$searchQuery'",
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary
            )

            Spacer(modifier = Modifier.height(12.dp))

            if (filteredListings.isEmpty()) {
                Box(
                    modifier = Modifier.weight(1f).fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    EmptyStateView(
                        title = "No Sublets Found",
                        description = "No campus sublets match '$searchQuery'. Try searching by city name, area, or room type."
                    )
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(bottom = 24.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    items(filteredListings, key = { it.id }) { listing ->
                        ListingCardItem(
                            listing = listing,
                            onClick = { onNavigateToListingDetails(listing.id) },
                            onToggleSave = { housingViewModel.toggleSaveListing(listing.id) }
                        )
                    }
                }
            }
        }
    }
}
