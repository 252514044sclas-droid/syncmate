package com.example.syncmate.ui.screens.fairrent

import androidx.compose.runtime.Composable
import com.example.syncmate.viewmodel.PredictionViewModel

/**
 * Dedicated Fair Price Predictor Screen matching the naming convention.
 * Delegates directly to the full 2026 Chennai FairRentScreen implementation.
 */
@Composable
fun FairPricePredictorScreen(
    viewModel: PredictionViewModel,
    onNavigateBack: () -> Unit
) {
    FairRentScreen(
        predictionViewModel = viewModel,
        onNavigateBack = onNavigateBack
    )
}
