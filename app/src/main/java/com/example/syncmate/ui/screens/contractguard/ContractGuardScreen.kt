package com.example.syncmate.ui.screens.contractguard

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
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
import com.example.syncmate.ui.components.AppHeader
import com.example.syncmate.ui.components.PrimaryButton
import com.example.syncmate.ui.components.SecondaryButton
import com.example.syncmate.ui.components.StatBadge
import com.example.syncmate.ui.theme.AmberWarning
import com.example.syncmate.ui.theme.SlateBackground
import com.example.syncmate.ui.theme.SlateCard
import com.example.syncmate.ui.theme.SlateCardBorder
import com.example.syncmate.ui.theme.TextPrimary
import com.example.syncmate.ui.theme.TextSecondary
import com.example.syncmate.util.CurrencyFormatter
import com.example.syncmate.viewmodel.ContractViewModel

@Composable
fun ContractGuardScreen(
    contractViewModel: ContractViewModel,
    onNavigateToUploadContract: () -> Unit,
    onNavigateToContractSummary: () -> Unit,
    onNavigateBack: () -> Unit
) {
    val uiState by contractViewModel.uiState.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SlateBackground)
            .safeDrawingPadding()
    ) {
        AppHeader(
            title = "Contract Guard AI",
            onBackClick = onNavigateBack
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(20.dp)
        ) {
            Text(
                text = "AI Legal Lease Analysis",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )

            Text(
                text = "Upload your sublease contract PDF to detect predatory clauses, hidden charges in INR, and notice period traps.",
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary,
                modifier = Modifier.padding(top = 6.dp, bottom = 20.dp)
            )

            PrimaryButton(
                text = "Upload Contract PDF",
                onClick = onNavigateToUploadContract
            )

            if (uiState.summary != null) {
                val summary = uiState.summary!!

                Spacer(modifier = Modifier.height(24.dp))

                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = SlateCard),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, SlateCardBorder, RoundedCornerShape(16.dp))
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        StatBadge(text = "ANALYSIS COMPLETE", backgroundColor = AmberWarning)

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = summary.fileName,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )

                        Text(
                            text = summary.summaryOverview,
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextSecondary,
                            modifier = Modifier.padding(top = 6.dp, bottom = 12.dp)
                        )

                        Row {
                            Text(text = "Monthly Rent: ", style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
                            Text(text = CurrencyFormatter.formatInr(summary.monthlyRent), style = MaterialTheme.typography.bodyMedium, color = TextPrimary, fontWeight = FontWeight.Bold)
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        Row {
                            Text(text = "Deposit Required: ", style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
                            Text(text = CurrencyFormatter.formatInr(summary.securityDeposit), style = MaterialTheme.typography.bodyMedium, color = TextPrimary, fontWeight = FontWeight.Bold)
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        SecondaryButton(
                            text = "View Detailed Legal Breakdown",
                            onClick = onNavigateToContractSummary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
