package com.example.syncmate.ui.screens.comparelistings

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.syncmate.data.model.HousingListing
import com.example.syncmate.ui.components.AppHeader
import com.example.syncmate.ui.theme.EmeraldSuccess
import com.example.syncmate.ui.theme.IndigoPrimary
import com.example.syncmate.ui.theme.SlateBackground
import com.example.syncmate.ui.theme.SlateCard
import com.example.syncmate.ui.theme.SlateCardBorder
import com.example.syncmate.ui.theme.TextPrimary
import com.example.syncmate.ui.theme.TextSecondary
import com.example.syncmate.util.CurrencyFormatter
import com.example.syncmate.viewmodel.HousingViewModel

@Composable
fun CompareListingsScreen(
    housingViewModel: HousingViewModel,
    onNavigateBack: () -> Unit
) {
    val uiState by housingViewModel.uiState.collectAsStateWithLifecycle()
    val listingsToCompare = if (uiState.compareListings.isNotEmpty()) {
        uiState.compareListings
    } else {
        uiState.listings.take(2)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SlateBackground)
            .safeDrawingPadding()
    ) {
        AppHeader(
            title = "Compare Sublets",
            onBackClick = onNavigateBack
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(20.dp)
        ) {
            Text(
                text = "Side-by-Side Comparison",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )

            Text(
                text = "Compare student sublet price in INR, campus proximity, room style, and amenities.",
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary,
                modifier = Modifier.padding(top = 4.dp, bottom = 16.dp)
            )

            BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
                val isNarrow = maxWidth < 400.dp

                val rowModifier = if (isNarrow) {
                    Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState())
                } else {
                    Modifier.fillMaxWidth()
                }

                Row(
                    modifier = rowModifier,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    listingsToCompare.forEach { listing ->
                        val cardModifier = if (isNarrow) {
                            Modifier.width(220.dp)
                        } else {
                            Modifier.weight(1f)
                        }

                        ComparisonCard(
                            listing = listing,
                            modifier = cardModifier
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun ComparisonCard(
    listing: HousingListing,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = SlateCard),
        modifier = modifier.border(1.dp, SlateCardBorder, RoundedCornerShape(16.dp))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = listing.title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = TextPrimary,
                maxLines = 2
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = CurrencyFormatter.formatMonthlyRent(listing.priceMonthly),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = IndigoPrimary
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(text = "Location / Campus:", style = MaterialTheme.typography.labelSmall, color = TextSecondary)
            Text(text = "${listing.address} (${listing.distanceToCampus})", style = MaterialTheme.typography.bodyMedium, color = TextPrimary)

            Spacer(modifier = Modifier.height(8.dp))

            Text(text = "Room Type:", style = MaterialTheme.typography.labelSmall, color = TextSecondary)
            Text(text = listing.roomType, style = MaterialTheme.typography.bodyMedium, color = TextPrimary)

            Spacer(modifier = Modifier.height(8.dp))

            Text(text = "Furnished:", style = MaterialTheme.typography.labelSmall, color = TextSecondary)
            Text(
                text = if (listing.isFurnished) "Fully Furnished" else "Unfurnished",
                style = MaterialTheme.typography.bodyMedium,
                color = TextPrimary
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(text = "Utilities Included:", style = MaterialTheme.typography.labelSmall, color = TextSecondary)
            Text(
                text = if (listing.utilitiesIncluded) "Yes (Included)" else "Paid Separately",
                style = MaterialTheme.typography.bodyMedium,
                color = if (listing.utilitiesIncluded) EmeraldSuccess else TextSecondary
            )
        }
    }
}
