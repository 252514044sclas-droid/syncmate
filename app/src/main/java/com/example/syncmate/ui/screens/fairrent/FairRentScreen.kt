package com.example.syncmate.ui.screens.fairrent

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
import com.example.syncmate.ui.components.ErrorMessage
import com.example.syncmate.ui.components.PrimaryButton
import com.example.syncmate.ui.components.StatBadge
import com.example.syncmate.ui.theme.EmeraldSuccess
import com.example.syncmate.ui.theme.IndigoPrimary
import com.example.syncmate.ui.theme.SlateBackground
import com.example.syncmate.ui.theme.SlateCard
import com.example.syncmate.ui.theme.SlateCardBorder
import com.example.syncmate.ui.theme.TextPrimary
import com.example.syncmate.ui.theme.TextSecondary
import com.example.syncmate.util.CurrencyFormatter
import com.example.syncmate.viewmodel.PredictionViewModel

@Composable
fun FairRentScreen(
    predictionViewModel: PredictionViewModel,
    onNavigateBack: () -> Unit
) {
    val uiState by predictionViewModel.uiState.collectAsStateWithLifecycle()

    var location by remember { mutableStateOf("Adyar, Chennai") }
    var sqFt by remember { mutableStateOf("650") }
    var bedrooms by remember { mutableStateOf("1") }
    var roomType by remember { mutableStateOf("Private Room") }
    var leaseDuration by remember { mutableStateOf("3") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SlateBackground)
            .safeDrawingPadding()
    ) {
        AppHeader(
            title = "AI Fair-Rent Predictor",
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
                text = "ML Fair-Rent Estimator",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )

            Text(
                text = "Predict fair market rent in Indian Rupees (INR) based on campus location, square footage, room type, and amenities.",
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary,
                modifier = Modifier.padding(top = 6.dp, bottom = 20.dp)
            )

            if (uiState.error != null) {
                ErrorMessage(message = uiState.error!!, modifier = Modifier.padding(bottom = 16.dp))
            }

            AppTextField(
                value = location,
                onValueChange = { location = it },
                label = "Location / Campus Zone (e.g. Adyar, Koramangala, Kothrud)"
            )

            Spacer(modifier = Modifier.height(14.dp))

            Row {
                AppTextField(
                    value = sqFt,
                    onValueChange = { sqFt = it },
                    label = "Area (Sq Ft)",
                    modifier = Modifier.weight(1f)
                )

                Spacer(modifier = Modifier.padding(horizontal = 6.dp))

                AppTextField(
                    value = bedrooms,
                    onValueChange = { bedrooms = it },
                    label = "Bedrooms",
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            AppTextField(
                value = roomType,
                onValueChange = { roomType = it },
                label = "Room Type (e.g. Private Room, Studio, 1BHK)"
            )

            Spacer(modifier = Modifier.height(14.dp))

            AppTextField(
                value = leaseDuration,
                onValueChange = { leaseDuration = it },
                label = "Lease Duration (Months)"
            )

            Spacer(modifier = Modifier.height(20.dp))

            PrimaryButton(
                text = "Calculate Fair Rent (INR)",
                isLoading = uiState.isLoading,
                onClick = {
                    predictionViewModel.predictFairRent(
                        location = location,
                        sqFtStr = sqFt,
                        bedroomsStr = bedrooms,
                        bathroomsStr = "1.0",
                        roomType = roomType,
                        leaseDurationStr = leaseDuration,
                        season = "Summer",
                        facilities = listOf("WiFi", "AC", "Power Backup")
                    )
                }
            )

            if (uiState.result != null) {
                val res = uiState.result!!

                Spacer(modifier = Modifier.height(28.dp))

                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = SlateCard),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, SlateCardBorder, RoundedCornerShape(16.dp))
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        StatBadge(text = "ML PREDICTION READY", backgroundColor = EmeraldSuccess)

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = "Predicted Fair Rent",
                            style = MaterialTheme.typography.titleMedium,
                            color = TextSecondary
                        )

                        Text(
                            text = CurrencyFormatter.formatMonthlyRentFull(res.predictedFairRent),
                            style = MaterialTheme.typography.displayLarge,
                            fontWeight = FontWeight.Bold,
                            color = IndigoPrimary
                        )

                        Text(
                            text = "Estimated Fair Range: ${CurrencyFormatter.formatInrRange(res.rentRangeLow, res.rentRangeHigh)}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextSecondary,
                            modifier = Modifier.padding(top = 4.dp)
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = "Estimated Utilities: ${CurrencyFormatter.formatMonthlyRent(res.estimatedUtilitiesMonthly)}",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )

                        if (res.marketInsights.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "Market Insights:",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            res.marketInsights.forEach { insight ->
                                Text(
                                    text = "• $insight",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = TextSecondary,
                                    modifier = Modifier.padding(top = 4.dp)
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
