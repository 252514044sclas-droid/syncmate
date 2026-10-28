package com.example.syncmate.ui.screens.filters

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.syncmate.ui.components.AppHeader
import com.example.syncmate.ui.components.PrimaryButton
import com.example.syncmate.ui.theme.IndigoPrimary
import com.example.syncmate.ui.theme.SlateBackground
import com.example.syncmate.ui.theme.TextPrimary
import com.example.syncmate.ui.theme.TextSecondary
import com.example.syncmate.util.CurrencyFormatter
import com.example.syncmate.viewmodel.HousingViewModel

@Composable
fun FiltersScreen(
    housingViewModel: HousingViewModel,
    onNavigateBack: () -> Unit
) {
    val uiState by housingViewModel.uiState.collectAsStateWithLifecycle()

    var maxPrice by remember { mutableStateOf(uiState.maxPriceFilter.toFloat()) }
    var furnishedOnly by remember { mutableStateOf(uiState.isFurnishedOnly) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SlateBackground)
            .safeDrawingPadding()
    ) {
        AppHeader(
            title = "Housing Filters",
            onBackClick = onNavigateBack
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(20.dp)
        ) {
            Text(
                text = "Max Monthly Rent: ${CurrencyFormatter.formatMonthlyRent(maxPrice)}",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )

            Text(
                text = "Adjust the maximum monthly rent budget for student sublets in Indian Rupees.",
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary,
                modifier = Modifier.padding(top = 4.dp, bottom = 12.dp)
            )

            Slider(
                value = maxPrice,
                onValueChange = { maxPrice = it },
                valueRange = 5000f..35000f,
                colors = SliderDefaults.colors(
                    thumbColor = IndigoPrimary,
                    activeTrackColor = IndigoPrimary
                ),
                modifier = Modifier.padding(vertical = 12.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "₹5,000",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextSecondary,
                    modifier = Modifier.weight(1f)
                )
                Text(
                    text = "₹35,000",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextSecondary
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Checkbox(
                    checked = furnishedOnly,
                    onCheckedChange = { furnishedOnly = it },
                    colors = CheckboxDefaults.colors(checkedColor = IndigoPrimary)
                )
                Text(
                    text = "Furnished Units Only (Bed, Study Desk, Storage)",
                    style = MaterialTheme.typography.bodyLarge,
                    color = TextPrimary
                )
            }

            Spacer(modifier = Modifier.height(36.dp))

            PrimaryButton(
                text = "Apply Filters",
                onClick = {
                    housingViewModel.updateFilters(
                        maxPrice = maxPrice.toDouble(),
                        roomType = "All",
                        furnished = furnishedOnly
                    )
                    onNavigateBack()
                }
            )

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
