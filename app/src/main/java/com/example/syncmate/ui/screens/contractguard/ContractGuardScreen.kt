package com.example.syncmate.ui.screens.contractguard

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.FormatQuote
import androidx.compose.material.icons.filled.Gavel
import androidx.compose.material.icons.filled.HourglassBottom
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.syncmate.data.model.FlaggedClause
import com.example.syncmate.ui.components.AppHeader
import com.example.syncmate.ui.components.PrimaryButton
import com.example.syncmate.ui.components.SecondaryButton
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
                text = "Upload your sublease contract PDF to detect predatory clauses, hidden charges in INR, and notice period traps with zero third-party exposure.",
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary,
                modifier = Modifier.padding(top = 6.dp, bottom = 18.dp)
            )

            PrimaryButton(
                text = if (uiState.summary != null) "Upload Another Contract" else "Upload Contract PDF",
                onClick = onNavigateToUploadContract
            )

            if (uiState.summary != null) {
                val summary = uiState.summary!!
                val riskScore = summary.overallRiskScore
                val criticalCount = summary.flaggedClauses.count { it.severity.equals("CRITICAL", ignoreCase = true) }

                // Theme styling based on calibrated scam risk score
                val (badgeText, badgeColor, badgeIcon) = when {
                    riskScore >= 70 -> Triple(
                        "$riskScore% SCAM RISK — DO NOT SIGN OR PAY",
                        Color(0xFFEF4444), // Crimson / RoseError
                        Icons.Default.Warning
                    )
                    riskScore >= 35 -> Triple(
                        "$riskScore% RISK — PREDATORY CLAUSES FOUND",
                        Color(0xFFF59E0B), // AmberWarning
                        Icons.Default.Warning
                    )
                    else -> Triple(
                        "$riskScore% RISK — SAFE LEASE",
                        Color(0xFF10B981), // EmeraldSuccess
                        Icons.Default.CheckCircle
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // ==========================================
                // 1. Prominent Scam Risk Ratio Gauge Card
                // ==========================================
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = SlateCard),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, badgeColor.copy(alpha = 0.5f), RoundedCornerShape(16.dp))
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        // Bold Gauge Badge
                        Surface(
                            shape = RoundedCornerShape(20.dp),
                            color = badgeColor.copy(alpha = 0.15f),
                            border = BorderStroke(1.dp, badgeColor.copy(alpha = 0.5f))
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                            ) {
                                Icon(
                                    imageVector = badgeIcon,
                                    contentDescription = null,
                                    tint = badgeColor,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = badgeText,
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = badgeColor
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Visual Risk Meter Progress Bar
                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "Scam Risk Probability",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = TextSecondary
                                )
                                Text(
                                    text = "$riskScore / 100",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = badgeColor
                                )
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            LinearProgressIndicator(
                                progress = { (riskScore / 100f).coerceIn(0f, 1f) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(8.dp)
                                    .clip(RoundedCornerShape(4.dp)),
                                color = badgeColor,
                                trackColor = MaterialTheme.colorScheme.surfaceVariant
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Document Title & Summary Overview
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.PictureAsPdf,
                                contentDescription = null,
                                tint = IndigoPrimary,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = summary.fileName,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                        }

                        Text(
                            text = summary.summaryOverview,
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextSecondary,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // ==========================================
                // 2. Extracted Financials & Terms Grid
                // ==========================================
                Text(
                    text = "Extracted Financial & Lease Terms",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Spacer(modifier = Modifier.height(10.dp))

                Row(modifier = Modifier.fillMaxWidth()) {
                    ContractMetricCard(
                        title = "Monthly Rent",
                        value = CurrencyFormatter.formatInr(summary.monthlyRent),
                        icon = Icons.Default.AttachMoney,
                        accentColor = EmeraldSuccess,
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    ContractMetricCard(
                        title = "Deposit Required",
                        value = CurrencyFormatter.formatInr(summary.securityDeposit),
                        icon = Icons.Default.Security,
                        accentColor = if (summary.securityDeposit > summary.monthlyRent * 3) RoseError else IndigoPrimary,
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(modifier = Modifier.fillMaxWidth()) {
                    ContractMetricCard(
                        title = "Notice Period",
                        value = "${summary.noticePeriodDays} Days",
                        icon = Icons.Default.HourglassBottom,
                        accentColor = if (summary.noticePeriodDays <= 7) RoseError else TextPrimary,
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    ContractMetricCard(
                        title = "Lease Duration",
                        value = summary.leaseTerm,
                        icon = Icons.Default.CalendarMonth,
                        accentColor = TextPrimary,
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                // ==========================================
                // 3. Red Flags Detected Section
                // ==========================================
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Red Flags Detected (${summary.flaggedClauses.size})",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )

                    if (criticalCount > 0) {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = RoseError.copy(alpha = 0.15f),
                            border = BorderStroke(1.dp, RoseError.copy(alpha = 0.4f))
                        ) {
                            Text(
                                text = "$criticalCount Critical",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = RoseError,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Display up to 3 highlighted clauses right on the main screen
                summary.flaggedClauses.take(3).forEach { clause ->
                    FlaggedClauseCard(clause = clause)
                    Spacer(modifier = Modifier.height(10.dp))
                }

                // ==========================================
                // 4. View Detailed Legal Breakdown Button
                // ==========================================
                Spacer(modifier = Modifier.height(10.dp))
                SecondaryButton(
                    text = "View Detailed Legal Breakdown (${summary.flaggedClauses.size} Clauses)",
                    onClick = onNavigateToContractSummary
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun ContractMetricCard(
    title: String,
    value: String,
    icon: ImageVector,
    accentColor: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(14.dp),
        color = SlateCard,
        border = BorderStroke(1.dp, SlateCardBorder),
        modifier = modifier
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = accentColor,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = title,
                    style = MaterialTheme.typography.labelSmall,
                    color = TextSecondary
                )
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = accentColor
            )
        }
    }
}

@Composable
fun FlaggedClauseCard(clause: FlaggedClause) {
    val isCritical = clause.severity.equals("CRITICAL", ignoreCase = true)
    val isHigh = clause.severity.equals("HIGH", ignoreCase = true)
    val sevColor = when {
        isCritical -> RoseError
        isHigh -> AmberWarning
        else -> IndigoPrimary
    }

    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = SlateCard),
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, SlateCardBorder, RoundedCornerShape(14.dp))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = clause.title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary,
                    modifier = Modifier.weight(1f)
                )
                StatBadge(
                    text = clause.severity.uppercase(),
                    backgroundColor = sevColor
                )
            }

            // Highlighted Quoted Sentence Box
            if (clause.clauseText.isNotBlank()) {
                Spacer(modifier = Modifier.height(10.dp))
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Icon(
                            imageVector = Icons.Default.FormatQuote,
                            contentDescription = "Quoted Text",
                            tint = IndigoPrimary,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
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
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = clause.explanation,
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary
                )
            }

            // Actionable Negotiation Advice
            if (clause.recommendation.isNotBlank()) {
                Spacer(modifier = Modifier.height(10.dp))
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = IndigoPrimary.copy(alpha = 0.1f),
                    border = BorderStroke(1.dp, IndigoPrimary.copy(alpha = 0.3f))
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Gavel,
                                contentDescription = null,
                                tint = IndigoPrimary,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Actionable Advice / Demand:",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = IndigoPrimary
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = clause.recommendation,
                            style = MaterialTheme.typography.bodySmall,
                            color = TextPrimary
                        )
                    }
                }
            }
        }
    }
}

