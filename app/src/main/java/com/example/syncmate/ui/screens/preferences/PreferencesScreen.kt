package com.example.syncmate.ui.screens.preferences

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
import com.example.syncmate.ui.components.AppHeader
import com.example.syncmate.ui.components.AppTextField
import com.example.syncmate.ui.components.PrimaryButton
import com.example.syncmate.ui.theme.SlateBackground
import com.example.syncmate.ui.theme.TextPrimary
import com.example.syncmate.ui.theme.TextSecondary

@Composable
fun PreferencesScreen(
    onNavigateBack: () -> Unit
) {
    var maxBudget by remember { mutableStateOf("15000") }
    var locationPref by remember { mutableStateOf("Adyar / Near Campus") }
    var lifestyleRhythm by remember { mutableStateOf("Early Riser • Clean & Quiet") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SlateBackground)
            .safeDrawingPadding()
    ) {
        AppHeader(
            title = "Housing Preferences",
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
                text = "Match Preferences",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )

            Text(
                text = "Set your target monthly budget in Indian Rupees and living rhythm to find ideal roommate matches.",
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary,
                modifier = Modifier.padding(top = 4.dp, bottom = 16.dp)
            )

            AppTextField(
                value = maxBudget,
                onValueChange = { maxBudget = it },
                label = "Target Monthly Budget (₹)",
                placeholder = "15000"
            )

            Spacer(modifier = Modifier.height(14.dp))

            AppTextField(
                value = locationPref,
                onValueChange = { locationPref = it },
                label = "Preferred Campus Neighborhood",
                placeholder = "e.g. Adyar, Koramangala, Kothrud"
            )

            Spacer(modifier = Modifier.height(14.dp))

            AppTextField(
                value = lifestyleRhythm,
                onValueChange = { lifestyleRhythm = it },
                label = "Living Rhythm & Study Habits",
                placeholder = "e.g. Early Riser, Night Owl, Quiet"
            )

            Spacer(modifier = Modifier.height(28.dp))

            PrimaryButton(
                text = "Save Preferences",
                onClick = { onNavigateBack() }
            )

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
