package com.example.syncmate.ui.screens.fakelisting

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import com.example.syncmate.ui.components.ErrorMessage
import com.example.syncmate.ui.components.PrimaryButton
import com.example.syncmate.ui.components.StatBadge
import com.example.syncmate.ui.theme.AmberWarning
import com.example.syncmate.ui.theme.EmeraldSuccess
import com.example.syncmate.ui.theme.IndigoPrimary
import com.example.syncmate.ui.theme.RoseError
import com.example.syncmate.ui.theme.SlateBackground
import com.example.syncmate.ui.theme.SlateCard
import com.example.syncmate.ui.theme.SlateCardBorder
import com.example.syncmate.ui.theme.TextPrimary
import com.example.syncmate.ui.theme.TextSecondary
import com.example.syncmate.viewmodel.FakeListingViewModel

@Composable
fun FakeListingScreen(
    fakeListingViewModel: FakeListingViewModel,
    onNavigateBack: () -> Unit
) {
    val uiState by fakeListingViewModel.uiState.collectAsStateWithLifecycle()

    var description by remember { mutableStateOf("Subletting my studio apartment. Asking $300/mo. Must send deposit via wire transfer upfront before viewing.") }
    var price by remember { mutableStateOf("300") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SlateBackground)
    ) {
        AppHeader(
            title = "Fake-Listing Shield",
            onBackClick = onNavigateBack
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(20.dp)
        ) {
            Text(
                text = "AI Scam & Fraud Detector",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )

            Text(
                text = "Paste a listing description or details to analyze for payment scams, price anomalies, and suspicious patterns.",
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary,
                modifier = Modifier.padding(top = 6.dp, bottom = 20.dp)
            )

            if (uiState.error != null) {
                ErrorMessage(message = uiState.error!!, modifier = Modifier.padding(bottom = 16.dp))
            }

            AppTextField(
                value = description,
                onValueChange = { description = it },
                label = "Listing Description / Message Text",
                singleLine = false,
                maxLines = 5,
                placeholder = "Paste description here..."
            )

            Spacer(modifier = Modifier.height(14.dp))

            AppTextField(
                value = price,
                onValueChange = { price = it },
                label = "Listed Monthly Rent ($)"
            )

            Spacer(modifier = Modifier.height(20.dp))

            PrimaryButton(
                text = "Scan Listing for Fraud",
                isLoading = uiState.isLoading,
                onClick = {
                    fakeListingViewModel.analyzeListing(description, price, emptyList())
                }
            )

            if (uiState.analysis != null) {
                val analysis = uiState.analysis!!

                Spacer(modifier = Modifier.height(24.dp))

                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = SlateCard),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, SlateCardBorder, RoundedCornerShape(16.dp))
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        val badgeColor = when (analysis.riskLevel) {
                            "HIGH_RISK" -> RoseError
                            "SUSPICIOUS" -> AmberWarning
                            else -> EmeraldSuccess
                        }

                        StatBadge(text = "RISK SCORE: ${analysis.riskScore}/100 - ${analysis.riskLevel}", backgroundColor = badgeColor)

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = "Analysis Explanation",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )

                        Text(
                            text = analysis.explanation,
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextSecondary,
                            modifier = Modifier.padding(top = 4.dp, bottom = 12.dp)
                        )

                        if (analysis.warningSignals.isNotEmpty()) {
                            Text(
                                text = "Flagged Warning Signals:",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )

                            analysis.warningSignals.forEach { signal ->
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = "• [${signal.type}]: ${signal.description}",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = RoseError
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
