package com.example.syncmate.ui.screens.contractsummary

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FormatQuote
import androidx.compose.material.icons.filled.Gavel
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.syncmate.ui.components.AppHeader
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
import com.example.syncmate.util.CurrencyFormatter
import com.example.syncmate.viewmodel.ContractViewModel

@Composable
fun ContractSummaryScreen(
    contractViewModel: ContractViewModel,
    onNavigateBack: () -> Unit
) {
    val uiState by contractViewModel.uiState.collectAsStateWithLifecycle()
    val summary = uiState.summary

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SlateBackground)
            .safeDrawingPadding()
    ) {
        AppHeader(
            title = "Detailed Legal Analysis",
            onBackClick = onNavigateBack
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(20.dp)
        ) {
            Text(
                text = "Clause-by-Clause Legal Audit",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )

            if (summary == null) {
                Text(
                    text = "No contract currently analyzed. Please upload a contract first.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary,
                    modifier = Modifier.padding(top = 16.dp)
                )
            } else {
                Spacer(modifier = Modifier.height(16.dp))

                val riskScore = summary.overallRiskScore
                val badgeColor = when {
                    riskScore >= 70 -> RoseError
                    riskScore >= 35 -> AmberWarning
                    else -> EmeraldSuccess
                }

                // Summary Overview Card
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = SlateCard),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, badgeColor.copy(alpha = 0.5f), RoundedCornerShape(16.dp))
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Key Agreement Terms",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = IndigoPrimary
                            )
                            StatBadge(
                                text = "$riskScore% RISK",
                                backgroundColor = badgeColor
                            )
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(text = "Lease Duration:", color = TextSecondary)
                            Text(text = summary.leaseTerm, color = TextPrimary, fontWeight = FontWeight.Bold)
                        }

                        Row(modifier = Modifier.fillMaxWidth().padding(top = 8.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(text = "Monthly Rent:", color = TextSecondary)
                            Text(text = CurrencyFormatter.formatInr(summary.monthlyRent), color = EmeraldSuccess, fontWeight = FontWeight.Bold)
                        }

                        Row(modifier = Modifier.fillMaxWidth().padding(top = 8.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(text = "Security Deposit:", color = TextSecondary)
                            Text(text = CurrencyFormatter.formatInr(summary.securityDeposit), color = TextPrimary, fontWeight = FontWeight.Bold)
                        }

                        Row(modifier = Modifier.fillMaxWidth().padding(top = 8.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(text = "Notice Period:", color = TextSecondary)
                            Text(text = "${summary.noticePeriodDays} Days", color = TextPrimary, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = "Flagged Clauses Requiring Negotiation (${summary.flaggedClauses.size})",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Text(
                    text = "Review the highlighted contract sentences and actionable advice below before signing or paying deposits.",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary,
                    modifier = Modifier.padding(top = 4.dp, bottom = 12.dp)
                )

                summary.flaggedClauses.forEach { clause ->
                    val isCritical = clause.severity.equals("CRITICAL", ignoreCase = true)
                    val isHigh = clause.severity.equals("HIGH", ignoreCase = true)
                    val sevColor = when {
                        isCritical -> RoseError
                        isHigh -> AmberWarning
                        else -> IndigoPrimary
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = SlateCard),
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, SlateCardBorder, RoundedCornerShape(16.dp))
                    ) {
                        Column(modifier = Modifier.padding(18.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = clause.title,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary,
                                    modifier = Modifier.weight(1f)
                                )
                                StatBadge(
                                    text = clause.severity.uppercase(),
                                    backgroundColor = sevColor
                                )
                            }

                            // Quoted contract sentence in highlighted quote box
                            if (clause.clauseText.isNotBlank()) {
                                Spacer(modifier = Modifier.height(12.dp))
                                Surface(
                                    shape = RoundedCornerShape(10.dp),
                                    color = MaterialTheme.colorScheme.surfaceVariant,
                                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(14.dp),
                                        verticalAlignment = Alignment.Top
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.FormatQuote,
                                            contentDescription = "Quoted Text",
                                            tint = IndigoPrimary,
                                            modifier = Modifier.size(20.dp)
                                        )
                                        Spacer(modifier = Modifier.width(10.dp))
                                        Text(
                                            text = "\"${clause.clauseText}\"",
                                            style = MaterialTheme.typography.bodySmall.copy(
                                                fontStyle = FontStyle.Italic,
                                                lineHeight = 18.sp
                                            ),
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                    }
                                }
                            }

                            if (clause.explanation.isNotBlank()) {
                                Spacer(modifier = Modifier.height(10.dp))
                                Text(
                                    text = clause.explanation,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = TextSecondary
                                )
                            }

                            // Actionable negotiation advice
                            if (clause.recommendation.isNotBlank()) {
                                Spacer(modifier = Modifier.height(12.dp))
                                Surface(
                                    shape = RoundedCornerShape(10.dp),
                                    color = IndigoPrimary.copy(alpha = 0.12f),
                                    border = BorderStroke(1.dp, IndigoPrimary.copy(alpha = 0.35f))
                                ) {
                                    Column(modifier = Modifier.padding(12.dp)) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(
                                                imageVector = Icons.Default.Gavel,
                                                contentDescription = null,
                                                tint = IndigoPrimary,
                                                modifier = Modifier.size(16.dp)
                                            )
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text(
                                                text = "What to Demand Before Signing:",
                                                style = MaterialTheme.typography.labelSmall,
                                                fontWeight = FontWeight.Bold,
                                                color = IndigoPrimary
                                            )
                                        }
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = clause.recommendation,
                                            style = MaterialTheme.typography.bodySmall,
                                            fontWeight = FontWeight.Medium,
                                            color = TextPrimary
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

