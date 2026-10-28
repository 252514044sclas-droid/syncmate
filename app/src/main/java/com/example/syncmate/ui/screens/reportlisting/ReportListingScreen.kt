package com.example.syncmate.ui.screens.reportlisting

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
import com.example.syncmate.ui.theme.EmeraldSuccess
import com.example.syncmate.ui.theme.SlateBackground
import com.example.syncmate.ui.theme.TextPrimary
import com.example.syncmate.ui.theme.TextSecondary
import com.example.syncmate.viewmodel.HousingViewModel

@Composable
fun ReportListingScreen(
    listingId: String,
    housingViewModel: HousingViewModel,
    onNavigateBack: () -> Unit
) {
    val uiState by housingViewModel.uiState.collectAsStateWithLifecycle()

    var reason by remember { mutableStateOf("Suspected Scam / Fake Rent Price") }
    var comments by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SlateBackground)
            .safeDrawingPadding()
    ) {
        AppHeader(
            title = "Report Listing Concern",
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
                text = "Report Sublet #${listingId.takeLast(6)}",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )

            Text(
                text = "Help keep SubletSync safe for students by flagging fraudulent, predatory, or misleading INR listings.",
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary,
                modifier = Modifier.padding(top = 6.dp, bottom = 20.dp)
            )

            if (uiState.reportSubmitted) {
                Text(
                    text = "✓ Report submitted successfully. Our safety team will investigate.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = EmeraldSuccess,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
            }

            AppTextField(
                value = reason,
                onValueChange = { reason = it },
                label = "Reason for Report"
            )

            Spacer(modifier = Modifier.height(14.dp))

            AppTextField(
                value = comments,
                onValueChange = { comments = it },
                label = "Additional Comments",
                singleLine = false,
                maxLines = 4,
                placeholder = "Describe why this listing seems suspicious or incorrect..."
            )

            Spacer(modifier = Modifier.height(28.dp))

            PrimaryButton(
                text = "Submit Report",
                isLoading = uiState.isLoading,
                onClick = {
                    housingViewModel.reportListing(listingId, reason, comments)
                }
            )

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
