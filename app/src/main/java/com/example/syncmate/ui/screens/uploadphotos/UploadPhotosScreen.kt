package com.example.syncmate.ui.screens.uploadphotos

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.syncmate.ui.components.AppHeader
import com.example.syncmate.ui.components.AppTextField
import com.example.syncmate.ui.components.PrimaryButton
import com.example.syncmate.ui.theme.SlateBackground
import com.example.syncmate.ui.theme.TextPrimary
import com.example.syncmate.ui.theme.TextSecondary
import com.example.syncmate.viewmodel.HousingViewModel

@Composable
fun UploadPhotosScreen(
    housingViewModel: HousingViewModel,
    onListingCreated: () -> Unit,
    onNavigateBack: () -> Unit
) {
    val uiState by housingViewModel.uiState.collectAsStateWithLifecycle()

    var title by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SlateBackground)
            .safeDrawingPadding()
    ) {
        AppHeader(
            title = "Post Sublet Listing",
            onBackClick = onNavigateBack
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .imePadding()
                .verticalScroll(rememberScrollState())
                .padding(20.dp)
        ) {
            Text(
                text = "Sublet Information",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )

            Text(
                text = "Fill in details to post your student sublet for verified peers with transparent INR rent.",
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary,
                modifier = Modifier.padding(top = 6.dp, bottom = 20.dp)
            )

            AppTextField(
                value = title,
                onValueChange = { title = it },
                label = "Listing Title",
                placeholder = "e.g. Sunny 1BHK Sublet Near Campus"
            )

            Spacer(modifier = Modifier.height(14.dp))

            AppTextField(
                value = address,
                onValueChange = { address = it },
                label = "Address / Campus Zone",
                placeholder = "e.g. Adyar, Chennai / Koramangala, Bengaluru"
            )

            Spacer(modifier = Modifier.height(14.dp))

            AppTextField(
                value = price,
                onValueChange = { price = it },
                label = "Monthly Rent (₹)",
                placeholder = "15000"
            )

            Spacer(modifier = Modifier.height(14.dp))

            AppTextField(
                value = description,
                onValueChange = { description = it },
                label = "Description & Lease Terms",
                singleLine = false,
                maxLines = 4,
                placeholder = "Describe furnished amenities, utilities included, distance to campus..."
            )

            Spacer(modifier = Modifier.height(28.dp))

            PrimaryButton(
                text = "Post Sublet (INR)",
                isLoading = uiState.isLoading,
                onClick = {
                    val priceVal = price.toDoubleOrNull() ?: 15000.0
                    housingViewModel.createListing(
                        title = title.ifBlank { "Student Sublet" },
                        address = address.ifBlank { "Near Campus" },
                        price = priceVal,
                        bedrooms = 1,
                        bathrooms = 1.0,
                        roomType = "Private Room",
                        description = description.ifBlank { "Verified student housing sublet." },
                        ownerName = "Alex Morgan",
                        ownerEmail = "alex.morgan@university.edu"
                    )
                    onListingCreated()
                }
            )

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
