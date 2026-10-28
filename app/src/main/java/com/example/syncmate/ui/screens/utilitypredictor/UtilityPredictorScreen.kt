package com.example.syncmate.ui.screens.utilitypredictor

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
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
import com.example.syncmate.ui.theme.IndigoPrimary
import com.example.syncmate.ui.theme.SlateBackground
import com.example.syncmate.ui.theme.SlateCard
import com.example.syncmate.ui.theme.SlateCardBorder
import com.example.syncmate.ui.theme.TextPrimary
import com.example.syncmate.ui.theme.TextSecondary
import com.example.syncmate.util.CurrencyFormatter
import com.example.syncmate.viewmodel.PredictionViewModel

@Composable
fun UtilityPredictorScreen(
    predictionViewModel: PredictionViewModel,
    onNavigateBack: () -> Unit
) {
    val uiState by predictionViewModel.uiState.collectAsStateWithLifecycle()

    var bedrooms by remember { mutableStateOf("2") }
    var season by remember { mutableStateOf("Summer") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SlateBackground)
            .safeDrawingPadding()
    ) {
        AppHeader(
            title = "Utility Cost Estimator",
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
                text = "Predict Monthly Utility Bills",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )

            Text(
                text = "Calculate estimated electric, water, internet, and maintenance bills in INR for your student flat.",
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary,
                modifier = Modifier.padding(top = 6.dp, bottom = 20.dp)
            )

            AppTextField(
                value = bedrooms,
                onValueChange = { bedrooms = it },
                label = "Number of Roommates / Bedrooms"
            )

            Spacer(modifier = Modifier.height(14.dp))

            AppTextField(
                value = season,
                onValueChange = { season = it },
                label = "Season (e.g. Summer, Monsoon, Winter)"
            )

            Spacer(modifier = Modifier.height(20.dp))

            PrimaryButton(
                text = "Estimate Utility Bills (INR)",
                isLoading = uiState.isLoading,
                onClick = {
                    predictionViewModel.predictFairRent(
                        location = "Adyar, Chennai",
                        sqFtStr = "800",
                        bedroomsStr = bedrooms,
                        bathroomsStr = "1.0",
                        roomType = "Apartment",
                        leaseDurationStr = "3",
                        season = season,
                        facilities = listOf("AC", "WiFi")
                    )
                }
            )

            if (uiState.result != null) {
                val res = uiState.result!!
                Spacer(modifier = Modifier.height(24.dp))

                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = SlateCard),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, SlateCardBorder, RoundedCornerShape(16.dp))
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Text(
                            text = "Estimated Utility Total",
                            style = MaterialTheme.typography.titleMedium,
                            color = TextSecondary
                        )

                        Text(
                            text = CurrencyFormatter.formatMonthlyRent(res.estimatedUtilitiesMonthly),
                            style = MaterialTheme.typography.displayLarge,
                            fontWeight = FontWeight.Bold,
                            color = IndigoPrimary
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = "Estimated Breakdown (per flat/month):",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )

                        res.utilityBreakdown.forEach { (name, cost) ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                            ) {
                                Text(text = name, color = TextSecondary, modifier = Modifier.weight(1f))
                                Text(text = CurrencyFormatter.formatInr(cost), color = TextPrimary, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
